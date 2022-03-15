/* Code segments originally written by Nigel Griffiths (nagg@uk.ibm.com)		*/
/* as part of the nmon for Linux tool see web pages:						*/
/*														*/
/* http://www.ibm.com/developerworks/eserver/articles/analyze_aix/     (external) 	*/
/* http://w3.aixncc.uk.ibm.com/tools/pmwiki.php (internal)   				*/


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
/*#include <curses.h> */
#include <signal.h>
#include <pwd.h>
#include <fcntl.h>
#include <math.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/utsname.h>
#include <sys/time.h>

#include <netinet/in.h>		/* Internet address structures  */
#include <sys/socket.h>		/* socket interface functions   */
#include <netdb.h>		/* host to IP resolution 	*/

#define	PORT		15050	/* port of "hello world" server */
#define P_CPUINFO	0
#define P_STAT		1
#define P_LPARINFO	2
#define P_MEMINFO   	3
#define P_VMSTAT	4
#define P_NETSTAT	5
#define P_DISKSTAT	6
#define P_NUMBER	7 /* one more than the max */
#define CPUMAX 64

char myhost[256];
int debug=0;
int cpus=1;
int loop;
int cpu_idle;
int cpu_user;
int cpu_sys;
int cpu_wait;
double cpu_sum,v1;
int maxloops = -1;
int seconds = -1;
char unixfile[1024];
double *cpu_peak;
int     max_cpus = 1; 
int n,i,pid,smt;
int flash_on=0;
int			rc;       /* system calls return value storage  */
int			s;        /* socket descriptor                  */
int			cs;       /* new connection's socket descriptor */
struct sockaddr_in	sa;       /* Internet address struct            */
struct sockaddr_in	csa;      /* client's address struct            */
int               	size_csa; /* size of client's address struct    */
char		mystr[256];
char        buffer[256];
char        tbuf[256];
long		tloc,next_minute;
unsigned long 	last_time_base,last_purr;
unsigned long   last_hpi=0;
double elapsed;

struct info_stat{
	long timebase;
        long clock;
};

struct info_vmstat{
	long pswpin;
        long pswpout;
};
struct info_netstat{
	long read;
        long write;
};
struct info_diskstat{
	long read;
        long write;
	long xfers;
};

struct cpu_stat {
	long user;
	long sys;
	long wait; /* sometimes nice */
	long idle;
        long procs_running;
};

struct mem_stat {
	long memtotal;
	long memfree;
	long memshared;
	long buffers;
	long cached;
	long swapcached;
	long active;
	long inactive;
	long hightotal;
	long highfree;
	long lowtotal;
	long lowfree;
	long swaptotal;
	long swapfree;
	long bigfree;
	long mapped;
	long shmem;
	long slab;
	long kernelstack;
	long pagetables;
	long commitlimit;
	long committed_as;
        long anonpages;
};

struct lpar_stat {
        char version[8]; 
        char serial[8];
	long partition_id;
	long DisWheRotPer;
	long MinEntCap;
	long MinEntCapPerVP;
	long MinMem;
	long MinProcs;
	long partition_max_entitled_capacity;
	long system_potential_processors;
	long DesEntCap;
	long DesMem;
	long DesProcs;
	long DesVarCapWt;
	long DedDonMode;
	long partition_entitled_capacity;
	long system_active_processors;
	long capacity_weight;
	long capped;
        long entitled_memory;
        long backing_memory;
	unsigned long purr;
	long partition_active_processors;
	long partition_potential_processors;
	long shared_processor_mode;
        long entitled_memory_pool_size;
	long cmo_faults;
};




struct data {
	struct cpu_stat cpu_total;
	struct cpu_stat cpuN[CPUMAX];
	struct mem_stat mem;
	struct lpar_stat lpar;
	struct info_stat info;
	struct info_vmstat vmstat;
	struct info_netstat netstat;
	struct info_diskstat diskstat;
#ifdef PARTITIONS
	struct part_stat parts[PARTMAX];
#endif /*PARTITIONS*/

	struct timeval tv;
	double time;

} database[2], *p, *q;




int reread =0;
struct {
	FILE *fp;
	char *filename;
	char buf[1024*4];
	char *line[128];
	int lines;
} proc[P_NUMBER];


double  doubletime(void)
{

        gettimeofday(&p->tv, 0);
        return((double)p->tv.tv_sec + p->tv.tv_usec * 1.0e-6);
}


proc_init()
{
int i;
	for(i=0;i<P_NUMBER;i++)
		proc[i].fp = 0;

	proc[P_CPUINFO].filename = "/proc/cpuinfo";
	proc[P_STAT].filename    = "/proc/stat";
	proc[P_VMSTAT].filename    = "/proc/vmstat";
	proc[P_LPARINFO].filename = "/proc/ppc64/lparcfg";
	proc[P_MEMINFO].filename  = "/proc/meminfo";
	proc[P_NETSTAT].filename  = "/proc/net/netstat";
	proc[P_DISKSTAT].filename  = "/proc/diskstats";
}

proc_read(int num)
{
int i;
int size;
int found;
	if(proc[num].fp == 0) {
		if( (proc[num].fp = fopen(proc[num].filename,"r")) == NULL) {
			perror("failed to open");
			printf("filename=%s\n",proc[num].filename);
		}
	}
	rewind(proc[num].fp);
	size = fread(proc[num].buf, 1, 1024*4-1, proc[num].fp);
	proc[num].buf[size]=0;
	proc[num].lines=0;
	proc[num].line[0]=&proc[num].buf[0];
	for(i=0;i<size;i++) {
		if(proc[num].buf[i] == '\t') 
			proc[num].buf[i]= ' '; 
		if(proc[num].buf[i] == '\n') {
			proc[num].lines++;
			proc[num].buf[i] = 0;
			proc[num].line[proc[num].lines] = &proc[num].buf[i+1];
		}
	}
	if(reread) {
		fclose( proc[num].fp);
		proc[num].fp = 0;
	}
}

void switcher(void)
{
	static int	which = 1;

	if (which) {
		p = &database[0];
		q = &database[1];
		which = 0;
	} else {
		p = &database[1];
		q = &database[0];
		which = 1;
	}
	if(flash_on)
		flash_on = 0;
	else
		flash_on = 1;
}

proc_mem()
{
int i,j;
i=proc[P_MEMINFO].lines;
	for(j=0;j<=i;j++) {
	sscanf(&proc[P_MEMINFO].line[j][0],"%s",&tbuf);	
        if (strncmp(tbuf,"MemTotal",8)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.memtotal);
        if (strncmp(tbuf,"MemFree",7)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.memfree);
        if (strncmp(tbuf,"Buffers",7)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.buffers);
        if (strncmp(tbuf,"Cached",6)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.cached);
        if (strncmp(tbuf,"SwapCached",10)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.swapcached);
        if (strncmp(tbuf,"Active:",7)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.active);
        if (strncmp(tbuf,"Inactive:",9)==0)
		sscanf(&proc[P_MEMINFO].line[j][17],  "%ld", &p->mem.inactive);
        if (strncmp(tbuf,"SwapTotal",9)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.swaptotal);
        if (strncmp(tbuf,"SwapFree",8)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.swapfree);
        if (strncmp(tbuf,"CommitLimit",11)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.commitlimit);
        if (strncmp(tbuf,"Committed_AS",12)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.committed_as);
        if (strncmp(tbuf,"Slab",4)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.slab);
        if (strncmp(tbuf,"PageTables",10)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.pagetables);
        if (strncmp(tbuf,"KernelStack",11)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.kernelstack);
        if (strncmp(tbuf,"Shmem",5)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.shmem);
        if (strncmp(tbuf,"AnonPages",9)==0)
		sscanf(&proc[P_MEMINFO].line[j][17], "%ld", &p->mem.anonpages);
	}
}

proc_cpu()
{
int i;
	sscanf(&proc[P_STAT].line[0][5], "%ld %ld %ld %ld", 
			&p->cpu_total.user,
			&p->cpu_total.wait,
			&p->cpu_total.sys,
			&p->cpu_total.idle);
/*
	for(i=0;i<cpus;i++ )
		sscanf(&proc[P_STAT].line[i+1][5], "%ld %ld %ld %ld", 
			&p->cpuN[i].user,
			&p->cpuN[i].wait,
			&p->cpuN[i].sys,
			&p->cpuN[i].idle);

*/
i=proc[P_STAT].lines-3;
sscanf(&proc[P_STAT].line[i][14], "%ld",&p->cpu_total.procs_running); 
}

proc_netstat()
{
int i,j;
long f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12;
f1=0;
i=proc[P_NETSTAT].lines;

sscanf(&proc[P_NETSTAT].line[i-1][0],"%s %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld",&tbuf,&f1,&f2,&f3,&f4,&f5,&f6,&f7,&f8,&f9,&f10,&f11,&f12);
/*
printf("netstat %s %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld\n",tbuf,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12);
*/
p->netstat.read=f7;
p->netstat.write=f8;
}

proc_diskstat()
{
int i,j;
long f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12;
i=proc[P_DISKSTAT].lines;
for(j=0;j<i;j++) {
sscanf(&proc[P_DISKSTAT].line[j][14],"%s %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld",&tbuf,&f1,&f2,&f3,&f4,&f5,&f6,&f7,&f8,&f9,&f10,&f11);
if (strlen(tbuf)==2)
{
	if (strncmp(tbuf,"da",2)==0)
/*
		printf("diskstat %s %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld %ld\n",tbuf,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11);
*/
	p->diskstat.read=f1;
	p->diskstat.write=f5;
	p->diskstat.xfers=f9;
}
} /* end for */
}

proc_vmstat()
{
int i,j;
i=proc[P_VMSTAT].lines;
	for(j=0;j<=i;j++) {
		sscanf(&proc[P_VMSTAT].line[j][0],"%s",&tbuf);
        	if (strncmp(tbuf,"pswpin",6)==0)
		{
        	sscanf(&proc[P_VMSTAT].line[j][7], "%ld",&p->vmstat.pswpin);
        	}
        	if (strncmp(tbuf,"pswpout",7)==0)
		{
        	sscanf(&proc[P_VMSTAT].line[j][8], "%ld",&p->vmstat.pswpout);
        	}
	}
}

proc_cpuinfo()
{
int i,j;
i=proc[P_CPUINFO].lines;
	for(j=0;j<=i;j++) {
		sscanf(&proc[P_CPUINFO].line[j][0],"%s",&tbuf);
        	if (strncmp(tbuf,"clock",5)==0) 
		{
        	sscanf(&proc[P_CPUINFO].line[j][9], "%ld",&p->info.clock);
                j=i-5;
        	}
        	if (strncmp(tbuf,"timebase",8)==0)
		{
        	sscanf(&proc[P_CPUINFO].line[j][11], "%ld",&p->info.timebase);
        	}
	}	
}


proc_lpar()
{
int i,j;
i=proc[P_LPARINFO].lines;
        p->lpar.entitled_memory_pool_size=0;
	for(j=0;j<=i;j++) {
	sscanf(&proc[P_LPARINFO].line[j][0],"%s",&tbuf);	
        if (strncmp(tbuf,"lparcfg",7)==0)
		sscanf(&proc[P_LPARINFO].line[j][8],  "%s" ,&p->lpar.version);	
        if (strncmp(tbuf,"serial_number",13)==0)
		sscanf(&proc[P_LPARINFO].line[j][21],  "%s" ,&p->lpar.serial);	
        if (strncmp(tbuf,"partition_id",12)==0)
		sscanf(&proc[P_LPARINFO].line[j][13],  "%ld" ,&p->lpar.partition_id);	
        if (strncmp(tbuf,"partition_entitled_capacity",27)==0)
		sscanf(&proc[P_LPARINFO].line[j][28],  "%ld", &p->lpar.partition_entitled_capacity);
        if (strncmp(tbuf,"system_active_processors",24)==0)
	{
		sscanf(&proc[P_LPARINFO].line[j][25],  "%ld", &p->lpar.system_active_processors);
	}
        if (strncmp(tbuf,"capacity_weight",15)==0)
		sscanf(&proc[P_LPARINFO].line[j][16],  "%ld", &p->lpar.capacity_weight);
        if (strncmp(tbuf,"capped",6)==0)
		sscanf(&proc[P_LPARINFO].line[j][7],  "%ld", &p->lpar.capped);
        if (strncmp(tbuf,"backing_memory",14)==0)
		sscanf(&proc[P_LPARINFO].line[j][15],  "%ld", &p->lpar.backing_memory);
        if (strncmp(tbuf,"purr",4)==0){
        	/*p->lpar.purr=strtoul(&proc[P_LPARINFO].line[j][5],NULL,12); */
		sscanf(&proc[P_LPARINFO].line[j][5],  "%ld", &p->lpar.purr); 
    		/*printf("purr from file=%ld \n",p->lpar.purr); */
		}
        if (strncmp(tbuf,"partition_active_processors",27)==0)
		sscanf(&proc[P_LPARINFO].line[j][28],  "%ld", &p->lpar.partition_active_processors);
        if (strncmp(tbuf,"shared_processor_mode",21)==0)
		sscanf(&proc[P_LPARINFO].line[j][22],  "%ld", &p->lpar.shared_processor_mode);
        if (strncmp(tbuf,"entitled_memory_pool_size",25)==0)
		sscanf(&proc[P_LPARINFO].line[j][26],  "%ld", &p->lpar.entitled_memory_pool_size);
        if (strncmp(tbuf,"cmo_faults",10)==0)
		sscanf(&proc[P_LPARINFO].line[j][11],  "%ld", &p->lpar.cmo_faults);
	}
}

void dostuff (int sock)
{	    
unsigned long purr,purr1,time_base,hpi;
long entitled_purr, unused_purr;
unsigned long delta_purr, delta_tb,delta_hpi;
double new_ent;
double phy_proc_consumed = 0, per_ent;	
long wblks,rblks,ibytes,obytes,pspin,pspout;

	
for(loop=1; ; loop++) {
		bzero(buffer,256);

		proc_read(P_STAT);
		proc_cpu();
		proc_read(P_MEMINFO);
		proc_mem();
		proc_read(P_CPUINFO);
		proc_cpuinfo();
		proc_read(P_LPARINFO);
		proc_lpar(); 
		proc_read(P_VMSTAT);
		proc_vmstat();
		proc_read(P_NETSTAT);
		proc_netstat();
		proc_read(P_DISKSTAT);
		proc_diskstat();
                                
		cpu_user = p->cpu_total.user - q->cpu_total.user; 
		cpu_sys  = p->cpu_total.sys  - q->cpu_total.sys; 
		cpu_wait = p->cpu_total.wait - q->cpu_total.wait; 
		cpu_idle = p->cpu_total.idle - q->cpu_total.idle; 
		cpu_sum = cpu_idle + cpu_user + cpu_sys + cpu_wait;

    rblks=p->diskstat.read - q->diskstat.read;
    wblks=p->diskstat.write - q->diskstat.write;
    ibytes=p->netstat.read - q->netstat.read;
    obytes=p->netstat.write - q->netstat.write;
    pspin=p->vmstat.pswpin - q->vmstat.pswpin;
    pspout=p->vmstat.pswpout - q->vmstat.pswpout;
				
				
    new_ent = (double)p->lpar.partition_entitled_capacity / 100.0 ;
    /* compute values to be displayed */
    /* 1- processor utilization ressource register */
    purr1 = cpu_sum;
    purr=p->lpar.purr;
    hpi=p->lpar.cmo_faults;
    delta_hpi=hpi-last_hpi;
    delta_purr = purr - last_purr;

	p->time = doubletime();
	elapsed = p->time - q->time;

    /* 2 - time elapsed */
    time_base = p->info.timebase;
    /*   delta_tb = time_base - last_time_base; */
    delta_tb = time_base;
    
    /* gives the physical pocessor consumed */
    phy_proc_consumed = (double)delta_purr / (double)time_base;
 
   phy_proc_consumed=phy_proc_consumed/elapsed;

    if (debug==1) printf("phys=%.3f \n",phy_proc_consumed); 

    if (phy_proc_consumed < 0.0) 
	phy_proc_consumed=0.0;
    if (phy_proc_consumed > (double)p->lpar.partition_active_processors)
    	phy_proc_consumed = (double)p->lpar.partition_active_processors;
 
    if ((int)p->lpar.capacity_weight == 0) {
     if (phy_proc_consumed > (double)p->lpar.partition_entitled_capacity/100.0)
	phy_proc_consumed = (double)p->lpar.partition_entitled_capacity/100.0;
    }
    
    if (debug==1)  printf("purr=%ld purr1=%ld deltap= %ld time_base=%ld delta_tb=%ld phys=%.2f elapsed=%.2f vp=%ld \n",
    purr,purr1,delta_purr,time_base,delta_tb,phy_proc_consumed,elapsed,p->lpar.partition_active_processors);
    
    per_ent = (double)((phy_proc_consumed / new_ent) * 100);
    rc=gethostname(myhost,256);
p->lpar.serial[5]=0;
/*PLS61*/	
sprintf(mystr,"PLS19,%s,%.1f,%.1f,%.1f,%.1f,%.2f,%.1f,%.1f,%.1f,%u,%s,%u,%u,%u,%s,%s,%u,%.1f,%.1f,%u,%u,0,0,0,%s,0,0,0,%.0f,%.0f,0,%.0f,%.0f,%.0f,%.0f,0,%.0f,%.0f,0,0,%u,%u,%u,0,%u,0,%u,%u,%u,%.1f,%.0f,0,%.0f,\n",  
				    p->lpar.shared_processor_mode ? "Shared" : "Dedicated",
				    (double)cpu_user / (double)cpu_sum * 100.0,
				    (double)cpu_sys  / (double)cpu_sum * 100.0,
				    (double)cpu_wait / (double)cpu_sum * 100.0,
				    (double)cpu_idle / (double)cpu_sum * 100.0,
				    (double)phy_proc_consumed,
        			per_ent,
                		(double)phy_proc_consumed,
        			per_ent,
				p->lpar.system_active_processors,
				p->lpar.capped ? "Capped" : "Uncapped",	
				p->lpar.capacity_weight,
	        		p->lpar.partition_active_processors,
				p->lpar.partition_entitled_capacity,
				smt ? "SMT On" : "SMT Off",
                                myhost,
				p->lpar.partition_id,    
				(double)p->mem.memtotal/1024.0,
				(double)p->mem.memfree/1024.0,
				pspin,
				pspout,
				p->lpar.serial,
				(double)(p->mem.active+p->mem.anonpages)/1024.0,
                                (double)(p->mem.slab+p->mem.buffers+p->mem.kernelstack)/1024.0,
                                (double)(p->mem.anonpages+p->mem.shmem+p->mem.active-p->mem.cached-p->mem.swapcached)/1024.0,
                                (double)(p->mem.cached+p->mem.swapcached)/1024.0,
                                (double)p->mem.swaptotal/1024.0,
				(double)p->mem.swapfree/1024.0,
				(double)p->mem.commitlimit/1024.0,
				(double)p->mem.committed_as/1024.0,
				p->diskstat.xfers,
				wblks*512,
				rblks*512,
				ibytes,
				obytes,
				p->cpu_total.procs_running,
                                delta_hpi,
                                p->info.clock*1000000.0,
                                (double)p->lpar.backing_memory/1024.0/1024.0,
                                (double)p->lpar.entitled_memory_pool_size/1024.0/1024.0);
				
/*printf("%s \n",mystr); 		*/		
	/* ok, we got data ... do the job... */

					n=write(sock,mystr, sizeof(mystr));
	/* ok, we got data ... do the job... */

					n=write(sock,mystr, sizeof(mystr));
						if (n<0) error("error reading from socket");
				      if (debug) printf("wrote %s %d bytes - \n",mystr,n);
					n=read(sock,buffer,sizeof(buffer));
        				if (n<0) error("error reading from socket");
					if (debug==1) printf("%s \n",buffer);

					if (strncmp(buffer,"exit",4)==0)
					{
						return;
					}

/*					if (debug==1) printf("%s \n",buffer); */

				switcher();
				    
    				last_purr = purr;
                                last_hpi=hpi;
    				last_time_base = time_base;
				reread=1;
	}				
}




main()
{
    signal(SIGCHLD,SIG_IGN);
    /* initiate machine's Internet address structure */
    /* first clear out the struct, to avoid garbage  */
    memset(&sa, 0, sizeof(sa));
    /* Using Internet address family */
    sa.sin_family = AF_INET;
    /* copy port number in network byte order */
    sa.sin_port = htons(PORT);
    /* we will accept cnnections coming through any IP	*/
    /* address that belongs to our host, using the	*/
    /* INADDR_ANY wild-card.				*/
    sa.sin_addr.s_addr = INADDR_ANY;

    /* allocate a free socket                 */
    /* Internet address family, Stream socket */
    s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) {
	perror("socket: allocation failed");
    }

    /* bind the socket to the newly formed address */
    rc = bind(s, (struct sockaddr *)&sa, sizeof(sa));

    /* check there was no error */
    if (rc) {
	perror("bind");
    }

    /* ask the system to listen for incoming connections	*/
    /* to the address we just bound. specify that up to		*/
    /* 5 pending connection requests will be queued by the	*/
    /* system, if we are not directly awaiting them using	*/
    /* the accept() system cal, when they arrive.		*/
    rc = listen(s, 5);

    /* check there was no error */
    if (rc) {
	perror("listen");
    }
    /* remember size for later usage */
    size_csa = sizeof(csa);
    /* enter an accept-write-close infinite loop */





	proc_init();
	/* Set parameters if not set by above */
	if (maxloops == -1)
		maxloops = 9999999;
	if (seconds  == -1)
		seconds = 2;
	/* To get the pointers setup */
	switcher();

	/* Request the Kernel addresses - if this fails stop now */
	readlink("/unix", unixfile, 1024);

	/* Initialise the time stamps for the first loop */
	p->time = doubletime();
	q->time = doubletime();

	cpu_peak = malloc(sizeof(double) * 128); /* MAGIC */
	for(i=0;i<(max_cpus+1);i++)
		cpu_peak[i]=0.0;

	switcher();
		
	proc_read(P_STAT);
        for(i=1;i<proc[P_STAT].lines;i++) {
                if(strncmp("cpu",proc[P_STAT].line[i],3) == 0)
                        max_cpus = cpus=i;
                else
                        break;
        }	
/*	printf("cpus= %u \n",max_cpus); */
        smt=0;
	if (max_cpus>p->lpar.partition_active_processors)
		smt=1;	

      proc_read(P_LPARINFO);
	proc_lpar(); 
	v1=atof(p->lpar.version);			
	rc=strncmp(p->lpar.version,"1.9",3);
	if (v1 != 1.9) {
                printf("/proc/ppc64/lparcfg is not version 1.9 \n");
                printf("You must use the right version of linuxagent \n");
                return;   
		kill(getpid(),SIGKILL);
		};
		
    	while (1) {
       	/* the accept() system call will wait for a	*/
       	/* connection, and when one is established, a	*/
       	/* new socket will be created to form it, and	*/
       	/* the csa variable will hold the address	*/
       	/* of the Client that just connected to us.	*/
       	/* the old socket, s, will still be available	*/
       	/* for future accept() statements.		*/
       	
   
	cs = accept(s, (struct sockaddr *)&csa, &size_csa);

       	/* check for errors. if any, enter accept mode again */
       	if (cs < 0)
    	    continue;
	    
	pid=fork();    
	if (pid <0)
		error("ERROR on fork");
	if (pid == 0) 	{
		close(s);
		dostuff(cs);
		kill(getpid(),SIGKILL);
		}
	else close(cs);
        
	} /* end while */
}

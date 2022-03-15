#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <libperfstat.h>
#include <netinet/in.h>		/* Internet address structures  */
#include <sys/socket.h>		/* socket interface functions   */
#include <netdb.h>		/* host to IP resolution 	*/
#include <sys/dr.h>
#include <sys/vminfo.h>
#include <cfgresid.h>		/* host to IP resolution 	*/


static int disp_util_header = 1;
int port=15050;

/* Data structures to hold old values */
static u_longlong_t last_purr, last_time_base;
static u_longlong_t last_vcpu_user, last_vcpu_sys, last_vcpu_idle, last_vcpu_wait;
static u_longlong_t last_phint = 0, last_vcsw = 0, last_pit = 0;
static u_longlong_t last_co = 0 ,last_ci = 0, last_hpi = 0;

static u_longlong_t last_xfers,last_wblks,last_rblks;
static u_longlong_t last_ipackets,last_ibytes,last_opackets,last_obytes;

static u_longlong_t last_pgspins,last_pgspouts,last_rq,last_sq;
static u_longlong_t last_scans,last_cycles,last_pgsteals;

static u_longlong_t last_sd0=0, last_sd1=0, last_sd2=0, last_sd3=0, last_sd4=0,
last_sd5=0;

static u_longlong_t last_stot=0, last_s3pull=0, last_s3push=0, last_s3grq=0;

struct dr_info drInfo;

/*void display_lpar_util(void);*/
int			rc,loop,debug;       
int			s;        /* socket descriptor                  */
int			cs;       /* new connection's socket descriptor */
struct sockaddr_in	sa;       /* Internet address struct            */
struct sockaddr_in	csa;      /* client's address struct            */
int               	size_csa; /* size of client's address struct    */
char		mystr[768];
char            buffer[768];
char            vers[8];
long		tloc,next_minute;
int 		n,i,pid;
int 		reconfig,drtype,drcmd;
size_t		bufsize;


void catch_reconfig(int sig_num)
{
    /* re-set the signal handler again to catch_int, for next time */
    signal(SIGRECONFIG,catch_reconfig); 

/*assume it's not PM */
    drtype=4;
/*get drtype */
    rc = dr_reconfig(DR_QUERY, &drInfo);

/*set drtype */

    if (drInfo.migrate) {
        if (drInfo.check) {
		drtype=1;
	} else if (drInfo.pre) {
		drtype=2;
	} else if (drInfo.post) {
                drtype=3;
        }
    } /* end drinfo.migrate */
    reconfig=1;
/* ack DR event */
    rc = dr_reconfig(DR_RECONFIG_DONE, &drInfo);

}


/********************************************************
* This function display_lpar_util(), displays the utilization
* metrics for the LPAR along with the LPAR details.
*********************************************************/
void display_lpar_util()
{
    u_longlong_t dlt_vcpu_user, dlt_vcpu_sys, dlt_vcpu_idle, dlt_vcpu_wait;
    u_longlong_t dlt_pgspins,dlt_pgspouts,frame_mem;
    u_longlong_t dlt_scans,dlt_cycles,dlt_pgsteals;
    u_longlong_t vcptimes;
    u_longlong_t purr, time_base;
   u_longlong_t entitled_purr, unused_purr;
    u_longlong_t delta_purr, delta_tb;
    u_longlong_t vcsw;
    u_longlong_t dlt_ci,dlt_co,ci,co;

   u_longlong_t stot,sd0,sd1,sd2,sd3,sd4,sd5,s3pull,s3push,s3grq;
   u_longlong_t dstot,dsd0,dsd1,dsd2,dsd3,dsd4,dsd5,ds3pull,ds3push,ds3grq;
 /*  double psd0,psd1,psd2,psd3,psd4,psd5; */

    double new_ent;
    double phy_proc_consumed = 0, per_ent;
    int i,j, rc,vsize;
    FILE *fp2;
    char path[16];
    char myserial[12];
   int retcode, cputotal;
   perfstat_id_t firstcpu;
   perfstat_cpu_t *statp;

    perfstat_partition_total_t partitionstats;
    perfstat_cpu_total_t cpustats;
    perfstat_memory_total_t minfo;
    perfstat_disk_total_t diskstats;
    perfstat_netinterface_total_t netstats;

    
    u_longlong_t xfers,wblks,rblks,rq,sq,hpi;
    u_longlong_t ipackets,ibytes,opackets,obytes;	

    lpar_info_format2_t lparinfo2;
    struct vminfo vmi;

    /* get the metrics */


  fp2 = popen("/usr/sbin/lscfg | grep vty0 | cut -d. -f 3 | cut -d- -f1", "r");
  if (fp2 == NULL) {
    printf("Failed to run command\n" );
    exit;
  }

  /* Read the output a line at a time - output it. */
  while (fgets(path, sizeof(path)-1, fp2) != NULL) {
    sscanf(path,"%s",myserial);
  }

  /* close */
  pclose(fp2);
   
    rc = perfstat_partition_total(NULL, &partitionstats,
                                  sizeof(perfstat_partition_total_t), 1);
    if (rc != 1) {
        perror("perfstat_partition_total");
        exit(-1);
    }
    perfstat_cpu_total(NULL,&cpustats,sizeof(perfstat_cpu_total_t),1);
    perfstat_memory_total(NULL,&minfo,sizeof(perfstat_memory_total_t),1);
    perfstat_disk_total(NULL,&diskstats,sizeof(perfstat_disk_total_t),1);
    perfstat_netinterface_total(NULL,&netstats,sizeof(perfstat_netinterface_total_t),1);
    lparinfo2.lpar_flags=LPAR_INFO2_EXTENDED;
    rc=lpar_get_info(2,&lparinfo2,sizeof(lpar_info_format2_t)); 
   cputotal =  perfstat_cpu(NULL, NULL, sizeof(perfstat_cpu_t), 0);

      /* check for error */
   if (cputotal <= 0)
   {
        perror("perfstat_cpu");
        exit(-1);
   }


   /* allocate enough memory for all the structures */
   statp = calloc(cputotal,sizeof(perfstat_cpu_t));
   /* set name to first cpu */
   strcpy(firstcpu.name, FIRST_CPU);

   /* ask to get all the structures available in one call */
   retcode = perfstat_cpu(&firstcpu, statp, sizeof(perfstat_cpu_t), cputotal);

   /* check for error */
   if (retcode <= 0)
   {
        perror("perfstat_cpu");
        exit(-1);
   }

/*     printf("lpar_get_info  %u flags: %u \n",rc,lparinfo2.lpar_flags); */
/* Print the header for utilization metrics */

    if (vmgetinfo((void *)&vmi,VMINFO,sizeof(vmi)) != 0)
	{
	perror("vmgetinfo failed");
	}
    ci=vmi.cmem_ncomp_ops;
    co=vmi.cmem_ndecomp_ops;
    dlt_ci=ci-last_ci;
    dlt_co=co-last_co;
    new_ent = (double)partitionstats.entitled_proc_capacity / 100.0 ;


    /* compute values to be displayed */
    /* 1- processor utilization ressource register */
    purr = partitionstats.puser + partitionstats.psys +
           partitionstats.pidle + partitionstats.pwait;
    delta_purr = purr - last_purr;
    /* 2 - time elapsed */
    time_base = partitionstats.timebase_last;
    delta_tb = time_base - last_time_base;
    /* gives the physical pocessor consumed */
    phy_proc_consumed = (double)delta_purr / (double)delta_tb;

    /* 3 - user/sys/idle/wait use computation */
    dlt_vcpu_user = partitionstats.puser - last_vcpu_user;
    last_vcpu_user = partitionstats.puser;
    dlt_vcpu_sys = partitionstats.psys - last_vcpu_sys;
    last_vcpu_sys = partitionstats.psys;
    dlt_vcpu_idle = partitionstats.pidle - last_vcpu_idle;
    last_vcpu_idle = partitionstats.pidle;
    dlt_vcpu_wait = partitionstats.pwait - last_vcpu_wait;
    last_vcpu_wait = partitionstats.pwait;


/* disk io */

	xfers=diskstats.xfers - last_xfers;
	last_xfers=diskstats.xfers;

	wblks=diskstats.wblks - last_wblks;
	last_wblks=diskstats.wblks;

	rblks=diskstats.rblks - last_rblks;
	last_rblks=diskstats.rblks;
    

/* net io */

	ipackets=netstats.ipackets - last_ipackets;
	last_ipackets=netstats.ipackets;

	ibytes=netstats.ibytes - last_ibytes;
	last_ibytes=netstats.ibytes;

	opackets=netstats.opackets - last_opackets;
	last_opackets=netstats.opackets;

	obytes=netstats.obytes - last_obytes;
	last_obytes=netstats.obytes;

/* affinity */

   sd0=0;
   sd1=0;
   sd2=0;
   sd3=0;
   sd4=0;
   sd5=0;
   stot=0;
   s3pull=0;
   s3push=0;
   s3grq=0;

   for (i = 0; i < retcode; i++) {
      sd0=sd0+statp[i].redisp_sd0;
      sd1=sd1+statp[i].redisp_sd1;
      sd2=sd2+statp[i].redisp_sd2;
      sd3=sd3+statp[i].redisp_sd3;
      sd4=sd4+statp[i].redisp_sd4;
      sd5=sd5+statp[i].redisp_sd5;
      s3pull=s3pull+statp[i].migration_S3pul;
      s3push=s3push+statp[i].migration_push;
      s3grq=s3grq+statp[i].migration_S3grq;
      }
      stot=sd0+sd1+sd2+sd3+sd4+sd5;
      dstot=stot-last_stot;
      ds3pull=s3pull-last_s3pull;
      ds3push=s3push-last_s3push;
      ds3grq=s3grq-last_s3grq;
      dsd0=sd0-last_sd0;
      dsd1=sd1-last_sd1;
      dsd2=sd2-last_sd2;
      dsd3=sd3-last_sd3;
      dsd4=sd4-last_sd4;
      dsd5=sd5-last_sd5;
/*
      psd0= 100 * ((double)dsd0 / (double)dstot);
      psd1= 100 * ((double)dsd1 / (double)dstot);
      psd2= 100 * ((double)dsd2 / (double)dstot);
      psd3= 100 * ((double)dsd3 / (double)dstot);
      psd4= 100 * ((double)dsd4 / (double)dstot);
      psd5= 100 * ((double)dsd5 / (double)dstot);
*/

/* paging */

    dlt_pgspins=minfo.pgspins-last_pgspins;
    last_pgspins=minfo.pgspins;

    dlt_pgspouts=minfo.pgspouts-last_pgspouts;
    last_pgspouts=minfo.pgspouts;

    dlt_scans=minfo.scans-last_scans;
    last_scans=minfo.scans;

    dlt_cycles=minfo.cycles-last_cycles;
    last_cycles=minfo.cycles;

    dlt_pgsteals=minfo.pgsteals-last_pgsteals;
    last_pgsteals=minfo.pgsteals;

/* runque*/

    rq=cpustats.runque-last_rq;
    last_rq=cpustats.runque;

    sq=cpustats.swpque-last_sq;
    last_sq=cpustats.swpque;

    hpi=partitionstats.hpi-last_hpi;
    last_hpi=partitionstats.hpi;

    vcptimes = dlt_vcpu_user + dlt_vcpu_sys + dlt_vcpu_idle + dlt_vcpu_wait;

    if ( partitionstats.type.b.shared_enabled ||  partitionstats.type.b.donate_enabled )
    { /* recompute some values to compensate shared mode errors */
        entitled_purr = delta_tb * new_ent;
        if (entitled_purr < vcptimes) {entitled_purr = vcptimes;}
        unused_purr = entitled_purr - vcptimes;
        dlt_vcpu_wait += unused_purr * ((double)
                         partitionstats.pwait /
                         (double)(partitionstats.pwait +
                         partitionstats.pidle));
        dlt_vcpu_idle += unused_purr * ((double)
                         partitionstats.pidle /
                         (double)(partitionstats.pwait +
                         partitionstats.pidle));
        vcptimes = entitled_purr;
 /*       dlt_vcpu_idle+=dlt_vcpu_wait;
        dlt_vcpu_wait-=dlt_vcpu_wait; */

    }
    else
    {
    phy_proc_consumed=(double)((100.0-(double)dlt_vcpu_idle*100.0/(double)vcptimes)/100.0)*partitionstats.online_cpus;
    }


    per_ent = (double)((phy_proc_consumed / new_ent) * 100);

/* formatting is important - ushort is %d size64_t is %lld */ 

	sprintf(mystr,
	"%s,%s,%.1f,%.1f,%.1f,%.1f,%.2f,%.1f,%.1f,%.1f,%u,%s,%u,%u,%u,SMT=%u,%s,%u,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%s,%u,%u,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%d,%lld,%llu,%u,%u,%.2f,%.2f,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,%llu,\n",
		vers,
		partitionstats.type.b.shared_enabled ? "Shared" : "Dedicated",	
		(double)dlt_vcpu_user*100.0/(double)vcptimes,
    		(double)dlt_vcpu_sys*100.0/(double)vcptimes,
    		(double)dlt_vcpu_wait*100.0/(double)vcptimes,
                (double)dlt_vcpu_idle*100.0/(double)vcptimes,
                (double)phy_proc_consumed,
        	per_ent,
                ((double)dlt_vcpu_user*100.0/(double)vcptimes)+
                         ((double)dlt_vcpu_sys*100.0/(double)vcptimes),
                (double)(partitionstats.pool_idle_time - last_pit) /
                         (double)delta_tb,
	        partitionstats.online_phys_cpus_sys,
		partitionstats.type.b.capped ? "Capped" : "Uncapped",	
		partitionstats.var_proc_capacity_weight,
		partitionstats.online_cpus,
		partitionstats.entitled_proc_capacity,
		partitionstats.smt_thrds,
		partitionstats.name,
		partitionstats.lpar_id,
		minfo.real_total*4096/1024/1024,
		minfo.real_free*4096/1024/1024,
		dlt_pgspins,
		dlt_pgspouts,
		dlt_scans,
   		dlt_cycles,
   		dlt_pgsteals,
	        myserial,	
		reconfig,
                drtype,
		partitionstats.online_memory,
		minfo.real_inuse*4096/1024/1024,
		minfo.real_system*4096/1024/1024,
		minfo.real_user*4096/1024/1024,
		minfo.real_process*4096/1024/1024,
		minfo.numperm*4096/1024/1024,
		minfo.pgsp_total*4096/1024/1024,
		minfo.pgsp_free*4096/1024/1024,
		minfo.pgsp_rsvd*4096/1024/1024,
		minfo.virt_total*4096/1024/1024,
		minfo.virt_active*4096/1024/1024,
		partitionstats.max_memory,
		partitionstats.min_memory,
		xfers,
		wblks,
		rblks,
		ipackets,
	        ibytes,
		opackets,
		obytes,
		rq,
		hpi,
		cpustats.processorHZ,		
		minfo.pmem/1024/1024,
		lparinfo2.vrm_pool_id,
		lparinfo2.vrm_pool_physmem/1024/1024,
                lparinfo2.ame_online_memory,
                lparinfo2.ame_factor,
       		lparinfo2.ame_type,
 		(double)vmi.ame_factor_tgt/100.0,
 		(double)vmi.ame_factor_actual/100.0,
		vmi.ame_deficit_size/1024/1024,
 		vmi.cmem_cpool_size/1024/1024,
 		vmi.cmem_cpool_free/1024/1024,
 		vmi.cmem_ucpool_size/1024/1024,
 		dlt_ci,
 		dlt_co,
                dsd0,
		dsd1,
		dsd2,
		dsd3,
		dsd4,
		dsd5,
		ds3pull,
                ds3push,
                ds3grq
		);

    /* Store the current values for the next iteration */
    last_vcsw = vcsw;
    last_phint = partitionstats.phantintrs;
    last_pit = partitionstats.pool_idle_time;
    last_purr = purr;
    last_time_base = time_base;
    last_ci=ci;
    last_co=co;
      last_sd0=sd0;
      last_sd1=sd1;
      last_sd2=sd2;
      last_sd3=sd3;
      last_sd4=sd4;
      last_sd5=sd5;
      last_stot=stot;
      last_s3pull=s3pull;
      last_s3push=s3push;
      last_s3grq=s3grq;

    return;
}


void dostuff (int sock)
{	    
	for(loop=1; ; loop++) {
	bzero(buffer,512);
			
	display_lpar_util();				
	/* ok, we got a new connection. do the job... */
	n=write(sock,mystr, sizeof(mystr));
	if (n<0) perror("error reading from socket");
	        if (debug==1) printf("wrote %s %d bytes \n",mystr,n);
		n=read(sock,buffer,sizeof(buffer));
       		if (n<0) perror("error reading from socket");
		if (debug == 1) printf("Received %s \n",buffer);
 		if (strncmp(buffer,"exit",5)==0)
		{
		break;
		}
		if (strncmp(buffer,"ack2",4)==0)
		{
	        reconfig=0;
		drtype=0;	
		}



	}				
}


/* main loop */
int main(int argc, char* argv[])
{
    int aixver,aixtl,aixsp;
    FILE *fp;
    char str[256];
    
    debug=0;
    if (argc>=1) 
	port=atoi(argv[1]);
    if (argc>=2)
	debug=atoi(argv[2]);
    drtype=0;
    reconfig=0;
/* check AIX versions */
    sprintf(vers,"%s","AIX613");

    if (access("/home/padmin",F_OK) == 0) 
    {
    /* is VIO server */
    sprintf(vers,"%s","AIX616");
    }
    else
    {
    /*   printf("padmin not found\n"); */
    fp=popen("oslevel -s","r");
    if (fp == NULL) {
        perror("popen oslevel");
        printf("popen error \n!");
        }
    else
    {
    if ( fgets(str,256,fp) != NULL ) {
         sscanf(str,"%d-%d-%d",&aixver,&aixtl,&aixsp);
         if (debug==1)
             printf("AIX vers tl sp %d %d %d\n",aixver,aixtl,aixsp);
         if ( aixver < 5300 ) {
         printf("AIX version not supported - 5.3TL12 is minimum level\n");
         exit(0);
	 }
         if ( aixver == 5300 ) {
            if (aixtl < 12 ) {
		printf("AIX 5.3 must be at TL12 or above \n");
    	        exit(0);
	        }
	 }
         if ( aixver == 6100 ) {
		if ( aixtl < 4 ) {
		printf("AIX 6.1 must be at TL4 or above \n");
    	        exit(0);
		}
		if (aixtl >= 4) {
			sprintf(vers,"%s","AIX616");
		}	
		if (aixtl >= 6) {
			sprintf(vers,"%s","AIX616");
		}	
	 }
         if ( aixver == 7100 ) {
		sprintf(vers,"%s","AIX614");
		if ( aixtl < 1 ) {
			printf("AIX 7.1 must be at TL1 or above \n");
    	        	exit(0);
		}
		if (aixtl >= 1) {
			sprintf(vers,"%s","AIX616");
		}	
	 }
	if (aixver > 7100 ) {
		sprintf(vers,"%s","AIX616");
		}
        /*else
        {
        printf("warning no AIX version detected \n");
        } */

    }
    pclose(fp);
    }  /* padmin check */

    if (debug==1)
    	printf("AIX version %s detected \n",vers);
    signal(SIGCHLD,SIG_IGN);
    signal(SIGRECONFIG,catch_reconfig);    

    /* initiate machine's Internet address structure */
    /* first clear out the struct, to avoid garbage  */
    memset(&sa, 0, sizeof(sa));
    /* Using Internet address family */
    sa.sin_family = AF_INET;
    /* copy port number in network byte order */
    sa.sin_port = htons(port);
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
    setsockopt(s,SOL_SOCKET,SO_REUSEADDR,(void *)&sa,sizeof(sa));

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
    while (1) {
   
	cs = accept(s, (struct sockaddr *)&csa, &size_csa);
       	/* check for errors. if any, enter accept mode again */
       	if (cs < 0)
    	    continue;
        system("sleep 2"); 
	pid=fork();    
	if (pid <0)
		perror("ERROR on fork");
	if (pid == 0) 	{
		close(s);
                dostuff(cs);
		kill(getpid(),SIGKILL);;    
	}
	else close(cs);
    } /* end while */
    return(0);
}
}

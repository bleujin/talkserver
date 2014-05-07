package net.ion.talk.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.talk.UserConnection;
import net.ion.talk.util.CalUtil;

public class HeartBeat {

	private ScheduledExecutorService worker;
	private int delaySecond;
	private AtomicReference<Boolean> started = new AtomicReference<Boolean>(Boolean.TRUE) ;
	
	public HeartBeat(ScheduledExecutorService worker){
		this(worker, 15) ;
	}
	
	public HeartBeat(ScheduledExecutorService worker, int delaySecond){
		this.worker = worker ;
		this.delaySecond = delaySecond ;
	}
	
	public void startBeat(final Runnable job){
		Callable<Void> heartBeatJob = new Callable<Void>() {
			public Void call() {
				if (! started.get()) return null;
				
				job.run(); 
				if (started.get()) worker.schedule(this, delaySecond, TimeUnit.MILLISECONDS);
				return null ;
			}
		} ;
		worker.schedule(heartBeatJob, 1, TimeUnit.SECONDS) ;
	}
	
	
	public HeartBeat delaySecond(int delaySecond){
		this.delaySecond = delaySecond ;
		return this ;
	}
	
	public void endBeat(){
		started.set(Boolean.FALSE);
	}
	
	public int waitSecond(){
		return delaySecond ;
	}
	
	public int overTimeSecond(){
		return delaySecond * 3 ;
	}

	public boolean isOverTime(UserConnection conn) {
        long heartBeatGap = (CalUtil.gmtTime() - conn.sessionTime()) / 1000 ;

        if(heartBeatGap < waitSecond()){
            return false;
        }else if(heartBeatGap < overTimeSecond()){
            conn.sendMessage("HEARTBEAT");
            return false;
        }else{
            return true ;
        }
		
	}
	
}

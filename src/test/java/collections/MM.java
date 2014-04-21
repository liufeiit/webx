package collections;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;

import javax.management.MBeanServer;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月21日 下午8:23:53
 */
public class MM {

	public static void main(String[] args) {
		ClassLoadingMXBean clmb = ManagementFactory.getClassLoadingMXBean();
		System.out.println("ClassLoadingMXBean : \n\t" 
				+ clmb.getLoadedClassCount() + ", \n\t" 
				+ clmb.getTotalLoadedClassCount() + ", \n\t"
				+ clmb.getUnloadedClassCount() + ", \n\t"
				+ clmb.isVerbose());
		CompilationMXBean cmb = ManagementFactory.getCompilationMXBean();
		System.out.println("CompilationMXBean : \n\t" 
				+ cmb.getName() + ", \n\t"
				+ cmb.getTotalCompilationTime() + ", \n\t"
				+ cmb.isCompilationTimeMonitoringSupported());
		List<MemoryManagerMXBean> mmmb = ManagementFactory.getMemoryManagerMXBeans();
		if(mmmb != null)
		for (MemoryManagerMXBean mm : mmmb) {
			System.out.println("MemoryManagerMXBean : \n\t"
					+ mm.getName() + ", \n\t"
					+ Arrays.toString(mm.getMemoryPoolNames()) + ", \n\t"
					+ mm.isValid());
		}
		MemoryMXBean mmb = ManagementFactory.getMemoryMXBean();
		System.out.println("MemoryMXBean : \n\t" + mmb.getObjectPendingFinalizationCount()
				+ mmb.getHeapMemoryUsage() + ", \n\t"
				+ mmb.getNonHeapMemoryUsage() + ", \n\t"
				+ mmb.isVerbose());
		List<MemoryPoolMXBean> mpbm = ManagementFactory.getMemoryPoolMXBeans();
		if(mpbm != null)
		for (MemoryPoolMXBean mp : mpbm) {
			System.out.println("MemoryPoolMXBean : \n\t"
//					+ mp.getCollectionUsageThreshold() + ", \n\t"
//					+ mp.getCollectionUsageThresholdCount() + ", \n\t"
					+ mp.getName() + ", \n\t"
//					+ mp.getUsageThreshold() + ", \n\t"
//					+ mp.getUsageThresholdCount() + ", \n\t"
					+ mp.getCollectionUsage() + ", \n\t"
					+ Arrays.toString(mp.getMemoryManagerNames()) + ", \n\t"
					+ mp.getPeakUsage() + ", \n\t"
//					+ mp.isCollectionUsageThresholdExceeded() + ", \n\t"
					+ mp.isCollectionUsageThresholdSupported() + ", \n\t"
//					+ mp.isUsageThresholdExceeded() + ", \n\t"
					+ mp.isValid());
		}
		OperatingSystemMXBean osmb = ManagementFactory.getOperatingSystemMXBean();
		System.out.println("OperatingSystemMXBean : \n\t"
				+ osmb.getArch() + ", \n\t"
				+ osmb.getAvailableProcessors() + ", \n\t"
				+ osmb.getName() + ", \n\t"
				+ osmb.getSystemLoadAverage() + ", \n\t"
				+ osmb.getVersion());
		MBeanServer ms = ManagementFactory.getPlatformMBeanServer();
		System.out.println("MBeanServer : \n\t" + ms.getDefaultDomain());
		RuntimeMXBean rmb = ManagementFactory.getRuntimeMXBean();
		System.out.println("RuntimeMXBean : \n\t" + rmb.getBootClassPath() + ", \n\t"
				+ rmb.getClassPath() + ", \n\t"
				+ rmb.getLibraryPath() + ", \n\t"
				+ rmb.getManagementSpecVersion() + ", \n\t"
				+ rmb.getName() + ", \n\t"
				+ rmb.getSpecName() + ", \n\t"
				+ rmb.getSpecVendor() + ", \n\t"
				+ rmb.getSpecVersion() + ", \n\t"
				+ rmb.getStartTime() + ", \n\t"
				+ rmb.getUptime() + ", \n\t"
				+ rmb.getVmName() + ", \n\t"
				+ rmb.getVmVendor() + ", \n\t"
				+ rmb.getVmVersion() + ", \n\t"
				+ rmb.getInputArguments() + ", \n\t"
				+ rmb.getSystemProperties() + ", \n\t"
				+ rmb.isBootClassPathSupported());
		ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
		System.out.println("ThreadMXBean : \n\t"
				+ tmb.getCurrentThreadCpuTime() + ", \n\t"
				+ tmb.getCurrentThreadUserTime() + ", \n\t"
				+ tmb.getDaemonThreadCount() + ", \n\t"
				+ tmb.getPeakThreadCount() + ", \n\t"
				+ tmb.getThreadCount() + ", \n\t"
				+ tmb.getTotalStartedThreadCount() + ", \n\t"
				+ Arrays.toString(tmb.findDeadlockedThreads()) + ", \n\t"
				+ Arrays.toString(tmb.findMonitorDeadlockedThreads()) + ", \n\t"
				+ Arrays.toString(tmb.getAllThreadIds()) + ", \n\t"
				+ tmb.isCurrentThreadCpuTimeSupported() + ", \n\t"
				+ tmb.isObjectMonitorUsageSupported() + ", \n\t"
				+ tmb.isSynchronizerUsageSupported() + ", \n\t"
				+ tmb.isThreadContentionMonitoringEnabled() + ", \n\t"
				+ tmb.isThreadCpuTimeEnabled() + ", \n\t"
				+ tmb.isThreadCpuTimeSupported());
	}
}

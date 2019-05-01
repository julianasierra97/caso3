package srv;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MonitorThread implements Runnable{

	
	private static PrintWriter printer;
	public static boolean termino;
	public MonitorThread() {
		this.termino=false;
		try {
			printer= new PrintWriter("Resultados/cpu.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	

	@Override
	public void run() {
		while(!termino)
		{
			try{
				Thread.sleep(50);
				printer.println(getProcessCpuLoad());
			//	System.out.println("Porcentaje del uso de CPU: " + getProcessCpuLoad());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		printer.close();
	}

	public static double getProcessCpuLoad() throws Exception {

		MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		if (list.isEmpty())     return Double.NaN;

		Attribute att = (Attribute)list.get(0);
		Double value  = (Double)att.getValue();

		// usually takes a couple of seconds before we get real values
		if (value == -1.0)      return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		return ((int)(value * 1000) / 10.0);
	}

}

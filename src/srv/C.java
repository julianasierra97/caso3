package srv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class C {
	public final static int TAMANO_POOL = 1;
	
	public static int numTransPerd = 0;
	
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO SIN SEGURIDAD: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int ip = Integer.parseInt(br.readLine());
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		
		int idThread = 0;
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");
		
		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		D.initCertificate(certSer, keyPairServidor);
		
//		D[] threads = new D[TAMANO_POOL];
//		
//		for (int i = 0; i < threads.length; i++) {
//			threads[i] = new D(null, i);
//		}
		
		ExecutorService executor = Executors.newFixedThreadPool(TAMANO_POOL);
		
	
		MonitorThread monitor = new MonitorThread();
		executor.execute(monitor);


		PrintWriter pw= new PrintWriter("Resultados/tiempo.txt", "UTF-8");
		int contador=0;
		
		while (true) {
			try { 
				Socket sc = ss.accept();
				
		
				System.out.println(MAESTRO + "Cliente " + idThread + " aceptado.");
				//D d = new D(sc,idThread);
				idThread++;
				
				//Asigno el socket para atender al cliente
				//threads[idThread].assignSocket(sc);
				D thread = new D(sc, idThread,pw);
				
				
				executor.execute(thread);
				contador++;
			//	System.out.println(contador);
			if (contador==400) {
				MonitorThread.termino=true;
				break;
				
			}
				
				//idThread++;
				
			
			} catch (IOException e) {
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
			
		}
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		pw.close();
		System.out.println("terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000terminoooo0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
	}
}
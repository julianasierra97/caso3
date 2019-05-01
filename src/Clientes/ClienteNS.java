package Clientes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class ClienteNS extends Thread{

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	


	public static X509Certificate generateCertificate(KeyPair llaves) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException
	{
		Date sd = new Date(System.currentTimeMillis());
		Date ed = new Date(System.currentTimeMillis() + 365*24*60*60*1000);
		BigInteger sn = new BigInteger("1234567890987654321");
		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		X500Principal subjectName = new X500Principal("CN=Test V3 Certificate");

		gen.setSerialNumber(sn);
		gen.setIssuerDN(subjectName);
		gen.setNotBefore(sd);
		gen.setNotAfter(ed);
		gen.setPublicKey(llaves.getPublic());
		gen.setSignatureAlgorithm("MD5withRSA");
		gen.setSubjectDN(subjectName);
		return gen.generate(llaves.getPrivate());
	}

	public ClienteNS(Socket s, PrintWriter pw, BufferedReader br) {
		socket = s;
		out = pw;
		in = br;
		
	}

	@Override
	public void run() {


		//Scanner sc = new Scanner(System.in);


		//Se comienza la comunicación con el servidor, se envía HOLA
		out.println("HOLA");
		System.out.println("HOLA");

		//Se solicitan los algoritmos a utilizar al cliente. 
		String enviar = "ALGORITMOS:";
		try{
			System.out.println("Escriba el algoritmo simétrico");
			//enviar += sc.nextLine();\
			enviar += "AES";

			System.out.println("Escriba el algoritmo asimétrico");
			enviar = enviar + ":" + "RSA";

			System.out.println("Escriba el algorimto HMAC");
			enviar = enviar + ":" + "HMACSHA1";

			out.println(enviar);
		}catch (Exception e) {
			e.printStackTrace();
		}

		//Verifica si existe algún error reportado por el servidor. 
		try{
			String serv = in.readLine();
			System.out.println("Servidor: " + serv);	
			if(serv.equals("ERROR"))
			{
				System.out.println("Error");
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Empieza envío del certificado");

		//Se genera el certificado y se envía al cliente.
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance(enviar.split(":")[2], new BouncyCastleProvider());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		generator.initialize(1024, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		try {
			X509Certificate cg = generateCertificate(keyPair);
			byte[] cgbytes = cg.getEncoded();
			String cgstring = DatatypeConverter.printHexBinary(cgbytes);
			out.println(cgstring);
			System.out.println("Cliente Cert" + cgstring);
		} catch (CertificateEncodingException | InvalidKeyException | IllegalStateException | NoSuchAlgorithmException
				| SignatureException e1) {
			e1.printStackTrace();
			return;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return;
		}


		//Se verifica si existe algún error reportado por el servidor. 
		try {
			String answer = in.readLine();
			System.out.println("Servidor Status 2 " + answer);
			if(answer.equals("ERROR"))
			{
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		//Se verifica el envío del certificado del servidor. 
		try{
			System.out.println("Servidor cert: " + in.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		//Se crea una llave simétrica para enviar al servidor. 
		KeyGenerator keyGen = null;
		SecretKey key = null;

		try{
			keyGen = KeyGenerator.getInstance(enviar.split(":")[1]);
			key = keyGen.generateKey();
			byte[] send = key.getEncoded();
			String s = DatatypeConverter.printHexBinary(send);
			System.out.println(s);
			out.println(s);

			//Se recibe la llave simétrica del servidor y se imprime. 
			System.out.println("Llave: " + in.readLine());
		}catch (Exception e) {
			e.printStackTrace();
		}

		//Se envía OK al servidor indicando que todo salió bien. 
		out.println("OK");

		//Se procede a enviar los datos al servidor. 
		//Scanner scan = new Scanner(System.in);
		System.out.println("Por favor ingrese los datos a enviar");
		//String datos = scan.nextLine();
		out.println("Hola");
		out.println("Hola");

		//Se reciben los datos enviados al servidor. 
		try{
			System.out.println(in.readLine());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

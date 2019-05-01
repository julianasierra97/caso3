package Clientes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class ClienteS {

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


	public static void main(String[] args) {


		if (args.length != 2) 
		{
			System.err.println("Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		System.out.println(hostName + portNumber);

		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		Scanner sc = new Scanner(System.in);
		try{
			socket = new Socket(hostName, portNumber);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch (Exception e) {
			e.printStackTrace();
		}


		try{
			//Empieza la comunicación con el servidor, envio HOLA
			out.println("HOLA");
		}catch (Exception e) {
			e.printStackTrace();
		}

		String enviar = "ALGORITMOS:";
		try{
			//Se solicita al cliente ingresas por consola los algoritmos a utilizar
			System.out.println("Escriba el algoritmo simétrico");
			enviar += sc.nextLine();

			System.out.println("Escriba el algoritmo asimétrico");
			enviar = enviar + ":" +sc.nextLine();

			System.out.println("Escriba el algorimto HMAC");
			enviar = enviar + ":" + sc.nextLine();

			out.println(enviar);
		}catch (Exception e) {
			e.printStackTrace();
		}
		

		try{
			//Verifica si hay algún error con la comunicación.
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

		//Se genera el certificado. 
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance(enviar.split(":")[2], new BouncyCastleProvider());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		}
		generator.initialize(1024, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		PublicKey puk;
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


		//Verifica si hay algún error reportado por el servidor
		try {
			String answer = in.readLine();
			System.out.println("Servidor Status" + answer);
			if(answer.equals("ERROR"))
			{
				return;
			}
		} catch (IOException e) {
			System.out.println("ERROR en 3");
			return;
		}

		//Se recibe el certificado del servidor y se guarda en un archivo.
		try{
			String cer = in.readLine();
			System.out.println("Servidor cert: " + cer);
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			byte[] serverCertificate = DatatypeConverter.parseHexBinary(cer);
			try (FileOutputStream fos = new FileOutputStream("./data/certificadoservidor.txt")) {
				fos.write(serverCertificate);
			}
			Certificate cert = cf.generateCertificate(new FileInputStream(new File("./data/certificadoservidor.txt")));
			puk = cert.getPublicKey();

		} catch (IOException | CertificateException e) {
			e.printStackTrace();
			return;

		}

		
		//Se crea la llave simétrica a enviar al servidor y 
		//se le envía cifrada con la llave publica recuperada del certificado. 
		KeyGenerator keyGen = null;
		SecretKey key = null;

		try{
			keyGen = KeyGenerator.getInstance(enviar.split(":")[1]);
			key = keyGen.generateKey();

			Cipher encrypt=Cipher.getInstance(enviar.split(":")[2]);
			encrypt.init(Cipher.ENCRYPT_MODE, puk);

			byte[] encryptedMessage=encrypt.doFinal(key.getEncoded());
			String s = DatatypeConverter.printHexBinary(encryptedMessage);
			System.out.println("Llave cliente: " + DatatypeConverter.printHexBinary(key.getEncoded()));
			out.println(s);
		}catch (Exception e)
		{
			e.printStackTrace();
			return;
		}	


		//Recibe la llave simétrica cifrada con la llave publica del cliente. 
		try{
			String llaveS = in.readLine();
			byte[] a = DatatypeConverter.parseHexBinary(llaveS);
			System.out.println("sin cifrar: " + llaveS);
			Cipher decrypt=Cipher.getInstance(enviar.split(":")[2]);
			decrypt.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			byte[] decryptedMessage=decrypt.doFinal(a);
			String descifre = DatatypeConverter.printHexBinary(decryptedMessage);
			System.out.println("La que es " + descifre);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//Se indica al servidor que todo llevo bien y se procede al envío de los datos. 
		out.println("OK");
		try{
			Cipher encrypt=Cipher.getInstance(enviar.split(":")[1]);
			encrypt.init(Cipher.ENCRYPT_MODE, key);
			Scanner scan = new Scanner(System.in);
			//Se solicita al cliente que escriba los datos. 
			System.out.println("Ingrese los datos");
			String datos = scan.nextLine();

			byte[] encryptedMessage=encrypt.doFinal(datos.getBytes());
			String s = DatatypeConverter.printHexBinary(encryptedMessage);
			out.println(s);
			
			//Se genera el digest.
			String digest = digest(enviar.split(":")[3], datos, key);
			System.out.println(digest);
			
			//Se envía al servidor.
			out.println(digest);



		}catch (Exception e) {
			e.printStackTrace();
			return;
		}

		//Se verifica que el servidor envíe el digest enviado, cifrado con la llave privada del servidor. 
		try{
			String mensaje = in.readLine();
			Cipher decrypt=Cipher.getInstance(enviar.split(":")[2]);
			decrypt.init(Cipher.DECRYPT_MODE, puk);
			byte[] eM = DatatypeConverter.parseHexBinary(mensaje);
			byte[] dM = decrypt.doFinal(eM);
			System.out.println(DatatypeConverter.printHexBinary(dM));
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * Tomado y adaptado de https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
	 * Método que encuentra el digest
	 * @param algoritmo Algoritmo a utilizar
	 * @param msg mensaje a hallar el digest
	 * @param key llave a utilizar
	 * @return
	 */
	public static String digest(String algoritmo, String msg, SecretKey key) {
		String digest = null;
		try {
			Mac mac = Mac.getInstance(algoritmo);
			mac.init(key);
			byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
			StringBuffer hash = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			digest = hash.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return digest;
	}

}

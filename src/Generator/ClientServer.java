package Generator;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Clientes.ClienteNS;
import uniandes.gload.core.Task;
import uniandes.gload.examples.clientserver.Client;

public class ClientServer extends Task{

	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		try {
			Socket s = new Socket("localhost", 10000);
			
			PrintWriter pw = new PrintWriter( s.getOutputStream( ), true );
		
			
			BufferedReader br = new BufferedReader( new InputStreamReader( s.getInputStream( ) ) );
			ClienteNS cliente = new ClienteNS(s, pw, br);
			cliente.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}

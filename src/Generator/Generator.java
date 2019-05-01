package Generator;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;
import uniandes.gload.examples.clientserver.generator.ClientServerTask;

public class Generator {
	
	private LoadGenerator generator;

	public Generator() {
		Task work = createTask();
		int numberOfTasks = 400;
		int gapBetweenTasks = 20;
		generator = new LoadGenerator("Client server load test ", numberOfTasks, work, gapBetweenTasks);
		generator.generate();
	}

	private Task createTask() {
		return new ClientServer();
	}
	
	public static void main(String[] args) {
		Generator gen = new Generator();
	}
	
	

}

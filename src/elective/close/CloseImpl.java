package elective.close;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import copal.CopalClient;

public class CloseImpl implements Close {

	private String state;
	private String name;

	public static final String s0 = "close"; // initial-final

	static String stateEventTypeName;
	static String publisherName;

	static final String REST_URI = "http://127.0.0.1:7878/copal/";
	static final String EVENTS_PATH = "events";
	static final String PUBLISHERS_PATH = "publishers";

	private Client client;
	private WebResource service;
	private WebResource eventTypeService;
	private WebResource publishersService;
	private WebResource myPublisher;

	public CloseImpl(String name) {
		this.name = name;
		publisherName = name;
		stateEventTypeName = "_" + name + "_state";
		state = s0;

		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);
		service = client.resource(REST_URI);
		eventTypeService = service.path(EVENTS_PATH);
		publishersService = service.path(PUBLISHERS_PATH);
		myPublisher = service.path(PUBLISHERS_PATH + "/" + publisherName);

		System.out.println("Registering events");
		ClientResponse response = eventTypeService.type(
				MediaType.APPLICATION_XML).put(ClientResponse.class,
				CopalClient.eventType(stateEventTypeName));
		System.out.println(response.toString());
		System.out.println(response.getEntity(String.class).toString());

		System.out.println("Setting ttl");
		WebResource test = service.path(EVENTS_PATH + "/" + stateEventTypeName
				+ "/ttl");
		response = test.type(MediaType.TEXT_PLAIN).put(ClientResponse.class,
				"" + Integer.MAX_VALUE);
		System.out.println(response.toString());
		System.out.println(response.getEntity(String.class).toString());

		String[] eventNames = { stateEventTypeName };
		System.out.println("Registering publisher");
		System.out.println(CopalClient.publisher(publisherName, eventNames));
		response = publishersService.type(MediaType.APPLICATION_XML).put(
				ClientResponse.class,
				CopalClient.publisher(publisherName, eventNames));
		System.out.println(response.toString());
		System.out.println(response.getEntity(String.class).toString());

		response = myPublisher.type(MediaType.APPLICATION_XML).post(
				ClientResponse.class,
				CopalClient.event(stateEventTypeName, state));
		System.out.println(response.toString());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Removing publisher");
				ClientResponse response = myPublisher
						.delete(ClientResponse.class);
				System.out.println(response.toString());
				System.out.println(response.getEntity(String.class).toString());

				System.out.println("Removing events");
				WebResource wr = service.path(EVENTS_PATH + "/"
						+ stateEventTypeName);
				response = wr.delete(ClientResponse.class);
				System.out.println(response.toString());
				System.out.println(response.getEntity(String.class).toString());
			}
		});
	}

	@Override
	public boolean b() {
		System.out.println("svolgo azione a");
		state = s0;

		ClientResponse response = myPublisher.type(MediaType.APPLICATION_XML)
				.post(ClientResponse.class,
						CopalClient.event(stateEventTypeName, state));
		System.out.println(response.toString());
		return true;
	}

	@Override
	public String getState() {
		return state;
	}

}

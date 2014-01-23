package net.ion.talk;

import java.io.Closeable;

import net.ion.radon.aclient.NewClient;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;

public class MockClient {

	private ToonServer tserver;
	private AradonClient fake;
	private NewClient real;

	public MockClient(ToonServer tserver) {
		this.tserver = tserver;
	}

	public static MockClient create(ToonServer tserver) {
		return new MockClient(tserver);
	}

	public synchronized AradonClient fake() {
		if (this.fake == null) {
			this.fake = AradonClientFactory.create(tserver.aradon());
		}
		return fake;
	}

	public synchronized NewClient real() {
		if (this.real == null) {
			this.real = NewClient.create();
		}
		return real;
	}

	public void close() {
		try {
			if (real != null)
				real.close();
			if (fake != null)
				fake.stop();
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
	}

}

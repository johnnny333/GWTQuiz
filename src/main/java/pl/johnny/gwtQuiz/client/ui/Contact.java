package pl.johnny.gwtQuiz.client.ui;

import java.util.Date;

public class Contact {
	final String address;
	final Date birthday;
	final String name;

	public Contact(String name, Date birthday, String address) {
		this.name = name;
		this.birthday = birthday;
		this.address = address;
	}
}

package pl.johnny.gwtQuiz.client.ui;

import java.util.Date;

public class Contact {
	final String address;
	final Date birthday;
	final String name;
	public boolean isThisCellEditable;

	public Contact(String name, Date birthday, String address, boolean isThisCellEditable) {
		this.name = name;
		this.birthday = birthday;
		this.address = address;
		this.isThisCellEditable = isThisCellEditable;
	}
}

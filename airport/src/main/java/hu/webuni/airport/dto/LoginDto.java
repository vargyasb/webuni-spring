package hu.webuni.airport.dto;

public class LoginDto {

	private String username;
	private String password;
	
	public LoginDto() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}

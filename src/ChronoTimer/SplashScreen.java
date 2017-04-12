package ChronoTimer;

public class SplashScreen extends UIPrint {

	final String display = "       ______  _          _       __  _    _  _      _  ____ ___ __ __ _ _ \n"
				   + "|\\/| /\\ |  |  | \\ /\\ |\\/|/ \\|\\ |/(_  / |_||_)/ \\|\\ |/ \\__||_) | /__/__|_|_)\n"
				   + "|  |/--\\|  |  |_//--\\|  |\\_/| \\| __) \\_| || \\\\_/| \\|\\_/  || \\_|_\\_|\\_||_| \\\n";
	
	@Override
	public String writeTo() {
		
		this.setText(display);
		return "start";
	}

	@Override
	public void up() {
		// TODO Auto-generated method stub

	}

	@Override
	public void down() {
		// TODO Auto-generated method stub

	}

}

package se.llbit.chunky.example;

import javax.swing.JOptionPane;

import se.llbit.chunky.api.Plugin;
import se.llbit.chunky.main.Chunky;

public class ExamplePlugin implements Plugin {

	@Override
	public void attach(Chunky chunky) {
		JOptionPane.showMessageDialog(null, "Welcome to the example plugin!");
	}
}

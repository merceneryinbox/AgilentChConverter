package AgilenBinaryReaders;

import javax.swing.*;
import java.nio.file.Path;

public class AgilentChFileChooser extends JFrame {

	// set up GUI
	public AgilentChFileChooser() {
		super("AgilentChFileChooser");
	}

	// allow user to specify file or directory name
	public Path getFileOrDirectoryPath() {
		// configur dialog allowing selection of a file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int result = fileChooser.showOpenDialog(this);
		// if user clicked Cancel button on dialog, return
		if (result == JFileChooser.CANCEL_OPTION) {
			System.exit(1);
		}

		// return Path representing the selected file
		return fileChooser.getSelectedFile().toPath();
	}
} // end class AgilentChFileChooser
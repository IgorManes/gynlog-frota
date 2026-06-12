package br.com.gynlog;

import br.com.gynlog.data.AppData;
import br.com.gynlog.ui.MainFrame;
import br.com.gynlog.ui.Theme;

import javax.swing.SwingUtilities;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.apply();
            AppData data = new AppData();
            MainFrame frame = new MainFrame(data);
            frame.setVisible(true);
        });
    }
}

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MinePanel extends JPanel {
    Playground playground = new Playground(this, 20, 20, 20, 20, 40, (new Random()).nextLong());
    Timer timer;
    Minesweeper parent;

    public MinePanel(Minesweeper nparent) {
        parent = nparent;
        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if(e.getX() < playground.cellsX * playground.cellWidth && e.getY() < playground.cellsY * playground.cellHeight && playground.handleMousePress(e) == 1) {
                        newgame();
                        parent.updateLabels();
                    }
                    repaint();
                }
            });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1001, 1001);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        playground.paint(g);
    }

    public void newgame() {
        if(JOptionPane.showConfirmDialog(new JFrame(), 
            "Soll ein neues Spiel mit den gleichen Einstellungen erzeugt werden?",
            "Neues Spiel",
            JOptionPane.YES_NO_OPTION) == 0) {
            playground = new Playground(this, 20, 20, playground.cellsX, playground.cellsY, playground.mineCount, (new Random()).nextLong());
        } else {
            boolean correctInput = false;
            String[] labels = {"Bitte Breite des Spielfelds eingeben:", "Bitte Höhe des Spielfelds eingeben:", "Bitte Anzahl der Minen  eingeben:", "Bitte einen Seed eingeben (freilassen für zufällige Generierung):"};
            String errorMessage;
            int[] outputs = new int[labels.length];
            int progress = 0;
            String s;
            while(!correctInput) {
                s = (String)JOptionPane.showInputDialog(new JFrame(), labels[progress], "Neues Spiel",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
                if(s == null) {
                    correctInput = false;
                    errorMessage = "Dieser Vorgang kann nicht abgebrochen werden!";
                } else {
                    correctInput = isNN(s);
                    if(progress != 3 && s.equals("")) correctInput = false;
                    errorMessage = "Bitte eine natürliche Zahl eingeben!";
                    if(correctInput) {
                        if(progress == 2 && strtoint(s) >= outputs[0] * outputs[1]) {
                            correctInput = false;
                            errorMessage = "Die Anzahl der Minen muss kleiner als die Anzahl der Zellen sein!";
                        } else if (progress < 2 && strtoint(s) > 50) {
                            errorMessage = "Das Spielfeld darf nicht größer als 20x20 Felder sein!";
                            correctInput = false;
                        }
                    }
                }
                if(!correctInput) {
                    JOptionPane.showMessageDialog(null, errorMessage);
                } else {
                    outputs[progress] = strtoint(s);
                    ++progress;
                    if(progress < labels.length) {
                        correctInput = false;
                    }
                }
            }
            if(outputs[3] == 0) {
                playground = new Playground(this, 20, 20, outputs[0], outputs[1], outputs[2], (new Random()).nextLong());
            } else {
                playground = new Playground(this, 20, 20, outputs[0], outputs[1], outputs[2], outputs[3]);
            }
        }
        parent.updateLabels();
        repaint();
    }

    public int strtoint(String s) {
        if(s.equals("")) {
            return 0;
        }
        return Integer.parseInt(s);
    }

    public static boolean isNN(String str) {
        if(str.equals("")) {
            return true;
        }
        return str.matches("\\d*") && Integer.parseInt(str) != 0;  //match a number with optional '-' and decimal.
    }

    public void cheat() {
        timer = new Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    parent.flagged.setText("Flagged cells: " + playground.flaggedCount);
                    parent.f.add(parent.flagged);
                    parent.discovered.setText("Discovered cells " + playground.cellsDiscovered);
                    parent.f.add(parent.discovered);
                    switch(playground.cheat()) {
                        case 0:
                        timer.stop();
                        break;
                        case 2:
                        JOptionPane.showMessageDialog(null, "Alle Minen entdeckt!");
                        newgame();
                        case 1:
                        repaint();
                        break;
                    }
                } 
            });
        timer.start();
    }
}
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Represents the application window
 * @author Chenhao Wei
 * @version 06.04.2021
 */
public class MainWindow extends JFrame implements ActionListener {
    // the combo box for from and to price selection
    private JComboBox<String> cbFrom;
    private JComboBox<String> cbTo;

    // back and forward button
    private JButton btnBack;
    private JButton btnForward;

    // current panel
    private int currentPanel;
    private JPanel centerPanel;
    private JPanel centerPanel1;
    private JPanel centerPanel2;
    private JPanel centerPanel3;

    // panel 1
    private JLabel lblWelcome;

    // data
    private ArrayList<AirbnbListing> data;

    // price range
    private boolean priceSelected;
    private int minPrice;
    private int maxPrice;

    /**
     * constructor
     */
    public MainWindow() {
        // set window size
        setSize(800, 600);

        // set the default operation when this window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // creates buttons and combo boxes
        btnBack = new JButton("    <    ");
        btnForward = new JButton("    >    ");
        cbFrom = new JComboBox<String>();
        cbTo = new JComboBox<String>();
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);

        cbFrom.addActionListener(this);
        cbTo.addActionListener(this);
        btnBack.addActionListener(this);
        btnForward.addActionListener(this);

        centerPanel = new JPanel(new BorderLayout());

        // arrange controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(new JLabel("From: "));
        topPanel.add(cbFrom);
        topPanel.add(new JLabel("  To: "));
        topPanel.add(cbTo);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.add(btnForward, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // load data
        loadFile();

        // panel 1
        lblWelcome = new JLabel("Welcome to London Property Marketplace");
        lblWelcome.setMinimumSize(new Dimension(0, 100));
        centerPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel1.add(lblWelcome, BorderLayout.CENTER);
        centerPanel1.setMinimumSize(new Dimension(0, 100));

        centerPanel2 = new CenterPanel2();

        centerPanel3 = new CenterPanel3();

        // update window
        updateDisplay();

        // show window
        setVisible(true);
    }

    // load data file
    private void loadFile() {
        AirbnbDataLoader loader = new AirbnbDataLoader();
        data = loader.load();
        ArrayList<String> priceOptions = new ArrayList<String>();
        for (int i = 0; i <= 10; i++) {
            priceOptions.add(i * 1000 + "");
        }

        // fill the combo box for price selection
        cbFrom.setModel(new DefaultComboBoxModel<String>(priceOptions.toArray(new String[0])));
        cbTo.setModel(new DefaultComboBoxModel<String>(priceOptions.toArray(new String[0])));
    }

    // display panel according the current panel
    private void updateDisplay() {
        centerPanel.removeAll();

        if (currentPanel == 0) {// first panel
            if (priceSelected) {
                lblWelcome.setText("<html><body>" + "Welcome to London Property Marketplace"
                        + "<br>" + "Range of Price: " + minPrice + " - " + maxPrice
                        + "</body></html>");
            }

            centerPanel.add(centerPanel1, BorderLayout.CENTER);
        } else if (currentPanel == 1) {// second panel
            centerPanel.add(centerPanel2, BorderLayout.CENTER);
        } else if (currentPanel == 2) {// third panel
            centerPanel.add(centerPanel3, BorderLayout.CENTER);
        }

        // update layout
        validate();
        doLayout();
        invalidate();
        repaint();
    }

    public static void main(String[] args) {
        // creates and display the main window
        new MainWindow();
    }

    // handle events of button
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbFrom) {
            // combobox for from price is clicked
            priceSelected = true;
            minPrice = Integer.parseInt(cbFrom.getSelectedItem().toString());
        }

        if (e.getSource() == cbTo) {
            // combobox for to price is clicked
            priceSelected = true;
            maxPrice = Integer.parseInt(cbTo.getSelectedItem().toString());
        }

        // back or forward button is clicked
        if (e.getSource() == btnBack || e.getSource() == btnForward) {
            if (!(minPrice <= maxPrice)) {
                JOptionPane.showMessageDialog(this, "Invalid price range");
                return;
            }

            if (e.getSource() == btnBack) {
                currentPanel--;
            }
            if (e.getSource() == btnForward) {
                currentPanel++;
            }
            currentPanel += 3;
            currentPanel %= 3;
        }

        // if price is selected, enable button
        if (priceSelected) {
            btnBack.setEnabled(true);
            btnForward.setEnabled(true);
        }

        // update window
        updateDisplay();
    }

    // definition of third panel
    private class CenterPanel3 extends JPanel implements ActionListener {
        private JButton left, right; // < and > button
        private JLabel title, value; // label for title and value

        // list of titles and values
        private ArrayList<String> titleStrings = new ArrayList<String>();
        private ArrayList<String> valueStrings = new ArrayList<String>();

        // index of current statistics item
        private int index;

        // Constructor
        public CenterPanel3() {
            setLayout(new BorderLayout());

            left = new JButton("    <    ");
            right = new JButton("    >    ");
            title = new JLabel();
            value = new JLabel();

            left.addActionListener(this);
            right.addActionListener(this);

            JPanel topPanel = new JPanel();
            topPanel.add(title);
            add(topPanel, BorderLayout.NORTH);
            JPanel centerJPanel = new JPanel(new BorderLayout());
            centerJPanel.add(value);
            value.setHorizontalAlignment(JLabel.CENTER);
            add(centerJPanel, BorderLayout.CENTER);
            add(left, BorderLayout.WEST);
            add(right, BorderLayout.EAST);

            // get statistics
            double average = 0;
            int sum = 0;
            String borough = "";
            double mostExpensive = 0;

            Map<String, Double> boroughMap = new HashMap<String, Double>();
            for (AirbnbListing item : data) {
                average += item.getNumberOfReviews();
                if (item.getRoom_type().equals("Entire home/apt")) {
                    sum++;
                }

                double expensive = item.getPrice() * item.getMinimumNights();
                if (boroughMap.containsKey(item.getNeighbourhood())) {
                    boroughMap.put(item.getNeighbourhood(),
                            boroughMap.get(item.getNeighbourhood()) + expensive);
                } else {
                    boroughMap.put(item.getNeighbourhood(), expensive);
                }
            }
            average /= data.size();

            for (String name : boroughMap.keySet()) {
                if (borough.isEmpty()) {
                    borough = name;
                } else {
                    if (boroughMap.get(name) > mostExpensive) {
                        mostExpensive = boroughMap.get(name);
                        borough = name;
                    }
                }
            }

            titleStrings.add("Average number of reviews per property");

            valueStrings.add(String.format("%.2f", average));

            titleStrings.add("Total number of available properties.");
            valueStrings.add(data.size() + "");

            titleStrings.add("The number of entire home and apartments");
            valueStrings.add(sum + "");

            titleStrings.add("The most expensive borough");
            valueStrings.add(borough + "");

            title.setText(titleStrings.get(index));
            value.setText(valueStrings.get(index));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == left) {
                index--;
            }
            if (e.getSource() == right) {
                index++;
            }

            index += titleStrings.size();
            index %= titleStrings.size();

            title.setText(titleStrings.get(index));
            value.setText(valueStrings.get(index));
        }
    }

    // the second panel
    private class CenterPanel2 extends JPanel {
        // positions of each borough
        Map<String, Integer> xMap = new HashMap<String, Integer>();
        Map<String, Integer> yMap = new HashMap<String, Integer>();

        public CenterPanel2() {
            setLayout(new BorderLayout());

            // set mouse listener
            // if a circle is clicked, pop up a window
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;

                    for (String blockName : xMap.keySet()) {
                        int mx = xMap.get(blockName);
                        int my = yMap.get(blockName);
                        mx += 25;
                        my += 25;

                        // get the distance of point(clicked) to center of circle
                        double distance = Math.sqrt(Math.pow(x - mx, 2) + Math.pow(y - my, 2));
                        if (distance < 25) { // point(clicked) is in circle
                            System.out.println(blockName);

                            JDialog dialog = new JDialog(getOwner());

                            String textString = "";

                            for (AirbnbListing item : data) {
                                String name = item.getNeighbourhood();
                                if (name.equals(blockName)) {
                                    textString += "Host Name: " + item.getHost_name() + "\tPrice: "
                                            + item.getPrice() + "\tNumber of Reviews: "
                                            + item.getNumberOfReviews() + "\tMinimum Nights: "
                                            + item.getMinimumNights() + "\n";
                                }
                            }

                            JTextArea textArea = new JTextArea(textString);
                            textArea.setEditable(false);
                            ScrollPane scrollPane = new ScrollPane();
                            scrollPane.add(textArea);
                            dialog.add(scrollPane);

                            dialog.setSize(600, 500);
                            dialog.setTitle(blockName);
                            dialog.setModal(true);
                            dialog.setVisible(true);

                            break;
                        }
                    }
                }
            });
        }

        // draw circles
        @Override
        public void paint(Graphics g) {
            super.paint(g);

            double minLat = Double.MAX_VALUE;
            double minLng = Double.MAX_VALUE;
            double maxLat = Double.MIN_VALUE;
            double maxLng = Double.MIN_VALUE;

            Map<String, Integer> blocksMap = new HashMap<String, Integer>();
            Map<String, Double> latMap = new HashMap<String, Double>();
            Map<String, Double> lngMap = new HashMap<String, Double>();

            // get average lat and lng for each borough
            for (AirbnbListing item : data) {
                String blockName = item.getNeighbourhood();
                blocksMap.put(blockName, 0);
                lngMap.put(blockName, 0.0);
                latMap.put(blockName, 0.0);
            }
            for (AirbnbListing item : data) {
                String blockName = item.getNeighbourhood();

                if (item.getLatitude() < minLat) {
                    minLat = item.getLatitude();
                }
                if (item.getLatitude() > maxLat) {
                    maxLat = item.getLatitude();
                }
                if (item.getLongitude() < minLng) {
                    minLng = item.getLongitude();
                }
                if (item.getLongitude() > maxLng) {
                    maxLng = item.getLongitude();
                }

                blocksMap.put(blockName, blocksMap.get(blockName) + 1);
                latMap.put(blockName, latMap.get(blockName) + item.getLatitude());
                lngMap.put(blockName, lngMap.get(blockName) + item.getLongitude());
            }

            // map lat and lng to the position in window
            for (String blockName : blocksMap.keySet()) {
                int num = blocksMap.get(blockName);
                double lat = latMap.get(blockName);
                double lng = lngMap.get(blockName);

                lat /= num;
                lng /= num;

                latMap.put(blockName, lat);
                lngMap.put(blockName, lng);
            }

            // collect the number of properties in the range of price
            for (AirbnbListing item : data) {
                String blockName = item.getNeighbourhood();
                blocksMap.put(blockName, 0);
            }

            for (AirbnbListing item : data) {
                if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                    String blockName = item.getNeighbourhood();

                    blocksMap.put(blockName, blocksMap.get(blockName) + 1);
                }
            }

            // get max number of properties
            int maxNum = 0;
            for (String blockName : blocksMap.keySet()) {
                int num = blocksMap.get(blockName);
                if (num > maxNum) {
                    maxNum = num;
                }
            }

            // draw circles
            for (String blockName : blocksMap.keySet()) {
                int num = blocksMap.get(blockName);

                double lat = latMap.get(blockName);
                double lng = lngMap.get(blockName);

                lat -= minLat;
                lng -= minLng;

                lat = lat * getWidth() / (maxLat - minLat);
                lng = lng * getHeight() / (maxLng - minLng);

                Color color = new Color(0, (int) (num * 1.0 * 255 / maxNum), 0);
                g.setColor(color);
                g.fillOval((int) lat, (int) lng, 50, 50);
                xMap.put(blockName, (int) lat);
                yMap.put(blockName, (int) lng);

                g.setColor(Color.red);
                g.drawString(blockName, (int) lat + 10, (int) lng + 10);
                g.drawString(num + "", (int) lat + 10, (int) lng + 35);
            }

        }
    }
}

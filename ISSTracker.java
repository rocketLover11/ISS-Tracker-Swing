import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import org.json.JSONObject;

public class ISSTracker extends JFrame {
    private JLabel latLabel;
    private JLabel lonLabel;
    private JLabel mapLabel;
    private Timer timer;

    public void ISSTrackerMap() {
        setTitle("ISS Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            Image icon = ImageIO.read(getClass().getResource("iss.png"));
            setIconImage(icon);
        } catch (IOException e) {}

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        latLabel = new JLabel("Latitude: loading...");
        lonLabel = new JLabel("Longitude: loading...");
        infoPanel.add(latLabel);
        infoPanel.add(lonLabel);
        add(infoPanel, BorderLayout.NORTH);

        mapLabel = new JLabel("Loading map...", SwingConstants.CENTER);
        add(mapLabel, BorderLayout.NORTH);

        timer = new Timer(1000, e -> updateISS());
        timer.start();

        updateISS();
        setVisible(true);
    }

    private void updateISS() {
        try {
            URL api = URL.of("http://api.open-notify.org/iss-now.json");
            HttpURLConnection c = (HttpURLConnection) api.openConnection();
            c.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder re = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) re.append(line);
            in.close();

            JSONObject json = new JSONObject(re.toString());
            JSONObject pos = json.getJSONObject("iss_position");

            String lat = pos.getString("latitude");
            String lon = pos.getString("longitude");

            latLabel.setText("Latitude: " + lat);
            lonLabel.setText("Longitude: " + lon);

            String mapUrl = String.format("https://staticmap.openstreetmap.org/staticmap.php?center=%s,%s&zoom=3&size=500x400&markers=%s,%s,red-pushpin", lat, lon, lat, lon);
            
            BufferedImage mapImage = ImageIO.read(URI.create(mapUrl).toURL());
            mapLabel.setIcon(new ImageIcon(mapImage));
            mapLabel.setText(null);
        } catch (Exception e) {
            latLabel.setText("Error fetching data");
            lonLabel.setText("");
        }
    }

    public static void main(Stringp[] args) {
        SwingUtilities.invokeLater(ISSTrackerMap::new);
    }
}
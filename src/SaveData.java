import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class exports the data contained within the observation list to a .csv file
 * @author Oliver Palmer
 *
 */
public class SaveData {
	
	private List<Observation> data;
	private FileWriter writer;
	
	public SaveData(List<Observation> data) {
		this.data = data;
		writeFile("file1.csv");
	}
	
	/**
	 * Creates a file, steps through each object in the list and writes the data to the file
	 * @param fileName - the filename
	 */
	public void writeFile(String fileName){
		
		try {
			writer = new FileWriter(fileName);
			
			writer.append("x, y, heading, value");
			writer.append('\n');
			
			for (Observation o : data) {
				writer.append(o.getX() +","+ o.getY() +","+ o.getHeading() +","+ o.getValue());
				writer.append("\n");
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

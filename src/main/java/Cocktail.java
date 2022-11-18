/**
 * The type Cocktail.
 */
public class Cocktail {

    private int id;
    private String name;
    private String instruction;
    private String imageURL;


    /**
     * public empty constructor needed for retrieving the POJO
     */
    public Cocktail() {
    }

    /**
     * Instantiates a new Cocktail.
     *
     * @param id the id
     */
    public Cocktail(int id) {
        this.id = id;
    }

    public Cocktail(int id, String name, String instruction, String imageURL) {
        this.id = id;
        this.name = name;
        this.instruction = instruction;
        this.imageURL = imageURL;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets instruction.
     *
     * @return the instruction
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Sets instruction.
     *
     * @param instruction the instruction
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Sets image url.
     *
     * @param imageURL the image url
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Cocktail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", instruction='" + instruction + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}

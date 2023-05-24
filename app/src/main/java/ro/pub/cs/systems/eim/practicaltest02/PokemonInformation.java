package ro.pub.cs.systems.eim.practicaltest02;

public class PokemonInformation {

    private String types;
    private String abilities;

    public PokemonInformation(String types, String abilities) {
        this.types = types;
        this.abilities = abilities;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getAbilities() {
        return abilities;
    }

    public void setAbilities(String abilities) {
        this.abilities = abilities;
    }

    @Override
    public String toString() {
        return "PokemonInformation{" +
                "types=" + types +
                ", abilities=" + abilities +
                '}';
    }
}

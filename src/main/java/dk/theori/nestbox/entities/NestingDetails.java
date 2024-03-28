package dk.theori.nestbox.entities;

import lombok.Data;

@Data
public class NestingDetails {
    String species;
    Integer eggs;
    Integer chicks;

    public NestingDetails() {
        species = "ubestemt";
        eggs = 0;
        chicks = 0;
    }
}

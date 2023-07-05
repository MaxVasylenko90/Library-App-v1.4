package ua.vasylenko.library.v13.Spring.Boot.DTO;

import java.util.List;

public class PeopleResponse {
    private List<PersonDTOAllFields> personDTOAllFieldsList;

    public PeopleResponse(List<PersonDTOAllFields> personDTOAllFieldsList) {
        this.personDTOAllFieldsList = personDTOAllFieldsList;
    }

    public List<PersonDTOAllFields> getPersonDTOWithAllFieldsList() {
        return personDTOAllFieldsList;
    }

    public void setPersonDTOWithAllFieldsList(List<PersonDTOAllFields> personDTOAllFieldsList) {
        this.personDTOAllFieldsList = personDTOAllFieldsList;
    }
}

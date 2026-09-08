package fr.diginamic.hello.controleurs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import fr.diginamic.hello.Departement;
import fr.diginamic.hello.services.DepartementService;
import fr.diginamic.hello.services.VilleService;

@WebMvcTest(DepartementControleur.class)
class DepartementControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartementService departementService;

    @MockitoBean
    private VilleService villeService;

    @Test
    void getDepartementsRetourneLaListeDesDepartements() throws Exception {
        when(departementService.extractDepartements()).thenReturn(List.of(
                new Departement("34", "Hérault"),
                new Departement("30", "Gard")));

        mockMvc.perform(get("/departements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("34"))
                .andExpect(jsonPath("$[0].nom").value("Hérault"))
                .andExpect(jsonPath("$[1].code").value("30"))
                .andExpect(jsonPath("$[1].nom").value("Gard"));

        verify(departementService).extractDepartements();
    }
}

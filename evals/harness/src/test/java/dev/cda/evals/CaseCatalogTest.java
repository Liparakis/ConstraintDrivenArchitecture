package dev.cda.evals;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class CaseCatalogTest {
    private static Path evalRoot() { return Path.of("..").toAbsolutePath().normalize(); }
    @Test void validatesEightFamiliesAndThreeMutations() throws Exception {
        CaseCatalog catalog = new CaseCatalog(evalRoot()); catalog.validate();
        assertEquals(11, catalog.all().size());
        assertEquals(8, catalog.all().stream().filter(c -> !c.isMutation()).count());
        assertEquals(3, catalog.all().stream().filter(Models.CaseEntry::isMutation).count());
    }
    @Test void mutationUsesBasePublicBriefAndPrivateOverride() throws Exception {
        CaseCatalog catalog = new CaseCatalog(evalRoot()); Models.CaseEntry e = catalog.find("01-appointment-booking.regulated-multitenant");
        assertTrue(catalog.publicFiles(e).containsKey("constraint-change.md"));
        assertTrue(Files.readString(catalog.publicFiles(e).get("brief.md")).contains("Appointment Booking"));
        assertTrue(Files.readString(catalog.expectationsPath(e)).contains("tenant"));
    }
}

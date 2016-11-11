package uk.gov.justice.maven.rules.service;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.File;
import java.util.Optional;

import net.diibadaaba.zipdiff.Differences;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactComparatorTest {

    private static final String EXISTING_FILE_PATTERN = "^((?!file4).)*$";
    private static final String NON_EXISTING_PATTERN = "^((?!blabla).)*$";

    @Mock
    private Log log;

    private ArtifactComparator artifactComparator;
    private File file1;
    private File file2;

    @Before
    public void setup() {
        ClassLoader classLoader = getClass().getClassLoader();
        file1 = new File(classLoader.getResource("jars/version1.jar").getFile());
        file2 = new File(classLoader.getResource("jars/version2.jar").getFile());
        artifactComparator = new ArtifactComparator(log);
    }

    @Test
    public void findDifferences() throws Exception {
        Optional<Differences> differencesOptional = artifactComparator.findDifferences(file1, file2, asList(EXISTING_FILE_PATTERN, NON_EXISTING_PATTERN));

        Differences differences = differencesOptional.get();

        assertThat(differences.getAdded().size(), is(1));
        assertThat(differences.getRemoved().size(), is(1));
        assertThat(differences.getChanged().size(), is(1));

        assertThat(differences.getAdded().get("file3"), notNullValue());
        assertThat(differences.getRemoved().get("file2"), notNullValue());
        assertThat(differences.getChanged().get("file1"), notNullValue());

        assertThat(differences.toString().contains("[added] file3"), is(true));
        assertThat(differences.toString().contains("[removed] file2"), is(true));
        assertThat(differences.toString().contains("[changed] file1"), is(true));
    }


}
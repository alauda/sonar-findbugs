package org.sonar.plugins.findbugs.profiles;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.plugins.findbugs.FindbugsProfileImporter;
import org.sonar.plugins.java.Java;

import java.io.InputStreamReader;
import java.io.Reader;

public class AlaudaProfile extends ProfileDefinition {
    private static final String FINDBUGS_ALL_IN_ONE_PROFILE_NAME = "FindBugs + FB-Contrib + Security + Sonar way";
    private final FindbugsProfileImporter importer;

    public AlaudaProfile(FindbugsProfileImporter importer) {
        this.importer = importer;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages messages) {
        Reader findbugsProfile = new InputStreamReader(this.getClass().getResourceAsStream(
                "/org/sonar/plugins/findbugs/profile-alauda.xml"));
        RulesProfile profile = importer.importProfile(findbugsProfile, messages);
        profile.setLanguage(Java.KEY);
        profile.setName(FINDBUGS_ALL_IN_ONE_PROFILE_NAME);
        return profile;
    }
}

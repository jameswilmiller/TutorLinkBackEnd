package com.tl.tutor_link.config;

import com.tl.tutor_link.tutor.model.*;
import com.tl.tutor_link.tutor.repository.CourseRepository;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Order(2)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!tutorRepository.findAll().isEmpty()) return;

        createDemoTutor(
                "Bill", "Nye", "bill.nye.demo@tutorlink.com", "billnye",
                "St Lucia QLD, Australia", -27.4975, 153.0137, true, 75,
                "Makes science feel less like homework and more like a controlled explosion. Strong believer that every problem can be solved with curiosity, evidence, and a bow tie.",
                "Science should be fun, explosive, and unforgettable.",
                "profile-images/bill-nye.jpg",
                List.of(Faculty.SCIENCE),
                List.of("PHYS1001", "PHYS1002", "CHEM1100", "CHEM1200"),
                List.<String[]>of(
                        new String[]{"Engaging", "Uses experiments and real-world examples"},
                        new String[]{"Enthusiastic", "High energy sessions that keep you hooked"},
                        new String[]{"Clear", "Breaks down complex science into simple ideas"}
                ),
                List.<String[]>of(
                        new String[]{"BSc Mechanical Engineering", "Cornell University", "1977"}
                )
        );

        createDemoTutor(
                "Neil", "deGrasse Tyson", "neil.tyson.demo@tutorlink.com", "neiltyson",
                "South Brisbane QLD, Australia", -27.4810, 153.0220, true, 90,
                "Will help you understand the cosmos and also remind you your assignment deadline is still very real.",
                "The universe is under no obligation to make sense to you — but I am.",
                "profile-images/neil-tyson.jpg",
                List.of(Faculty.SCIENCE),
                List.of("PHYS1001", "PHYS1002", "MATH1051", "MATH1052"),
                List.<String[]>of(
                        new String[]{"Conceptual", "Focuses on deep understanding over memorisation"},
                        new String[]{"Inspiring", "Makes you care about the subject"},
                        new String[]{"Rigorous", "High standards, high results"}
                ),
                List.<String[]>of(
                        new String[]{"PhD Astrophysics", "Columbia University", "1991"},
                        new String[]{"BSc Physics", "Harvard University", "1980"}
                )
        );

        createDemoTutor(
                "Gordon", "Ramsay", "gordon.ramsay.demo@tutorlink.com", "gordonramsay",
                "Fortitude Valley QLD, Australia", -27.4570, 153.0340, false, 85,
                "Your work is RAW until proven otherwise. Expect brutally honest feedback and significantly improved results.",
                "Perfection is a lot of little things done well.",
                "profile-images/gordon-ramsay.png",
                List.of(Faculty.SCIENCE, Faculty.BUSINESS_ECONOMICS_LAW),
                List.of("FOOD2000", "TOUR1000", "MGTS1301"),
                List.<String[]>of(
                        new String[]{"Direct", "No sugarcoating — honest feedback only"},
                        new String[]{"Hands-on", "Learn by doing, not just watching"},
                        new String[]{"Demanding", "High expectations lead to high achievement"}
                ),
                List.<String[]>of(
                        new String[]{"Hotel Management Diploma", "North Oxfordshire Technical College", "1987"},
                        new String[]{"3 Michelin Stars", "Restaurant Gordon Ramsay", "2001"}
                )
        );

        createDemoTutor(
                "David", "Attenborough", "david.attenborough.demo@tutorlink.com", "davidattenborough",
                "New Farm QLD, Australia", -27.4670, 153.0450, true, 70,
                "Calmly explains biology so well you might accidentally enjoy studying. Narration voice not guaranteed but highly likely.",
                "It's surely our responsibility to do everything within our power to create a planet that provides a home for all life.",
                "profile-images/david-att.jpg",
                List.of(Faculty.SCIENCE, Faculty.HEALTH_MEDICINE_BEHAVIOURAL_SCIENCES),
                List.of("BIOL1020", "BIOL1030", "BIOL1040", "BIOL2200", "ERTH1000"),
                List.<String[]>of(
                        new String[]{"Patient", "Takes time to ensure every concept is understood"},
                        new String[]{"Storytelling", "Weaves narrative into every lesson"},
                        new String[]{"Observational", "Teaches you to notice and question the world"}
                ),
                List.<String[]>of(
                        new String[]{"BA Natural Sciences", "Cambridge University", "1947"},
                        new String[]{"MA Natural Sciences", "Cambridge University", "1950"}
                )
        );

        createDemoTutor(
                "Marie", "Curie", "marie.curie.demo@tutorlink.com", "mariecurie",
                "Toowong QLD, Australia", -27.4850, 152.9920, false, 80,
                "Serious about chemistry, precision, and not guessing answers. Helps students shine without the radiation.",
                "Nothing in life is to be feared, it is only to be understood.",
                "profile-images/marie-curie.jpg",
                List.of(Faculty.SCIENCE),
                List.of("CHEM1090", "CHEM1100", "CHEM1200", "CHEM2050", "CHEM2060", "PHYS1001"),
                List.<String[]>of(
                        new String[]{"Precise", "Detail-oriented and methodical in every lesson"},
                        new String[]{"Research-focused", "Teaches you how to think like a scientist"},
                        new String[]{"Disciplined", "Structured sessions with clear goals"}
                ),
                List.<String[]>of(
                        new String[]{"PhD Physics", "University of Paris", "1903"},
                        new String[]{"Nobel Prize Chemistry", "Royal Swedish Academy", "1911"}
                )
        );

        createDemoTutor(
                "Albert", "Einstein", "albert.einstein.demo@tutorlink.com", "alberteinstein",
                "Indooroopilly QLD, Australia", -27.4990, 152.9730, true, 95,
                "Breaks down complex problems simply. Hair may be chaotic, explanations are not.",
                "Imagination is more important than knowledge.",
                "profile-images/albert-einstein.jpg",
                List.of(Faculty.SCIENCE),
                List.of("MATH1051", "MATH1052", "MATH2001", "PHYS1001", "PHYS1002"),
                List.<String[]>of(
                        new String[]{"Conceptual", "Builds intuition before diving into formulas"},
                        new String[]{"Patient", "Happy to revisit ideas from different angles"},
                        new String[]{"Creative", "Encourages thinking outside conventional methods"}
                ),
                List.<String[]>of(
                        new String[]{"PhD Physics", "University of Zurich", "1905"},
                        new String[]{"Nobel Prize Physics", "Royal Swedish Academy", "1921"}
                )
        );

        createDemoTutor(
                "Taylor", "Swift", "taylor.swift.demo@tutorlink.com", "taylorswift",
                "West End QLD, Australia", -27.4815, 153.0090, true, 88,
                "Turns essays into compelling narratives. Known for helping students rewrite their worst drafts into something impressive.",
                "Long story short, I survived.",
                "profile-images/taylor-swift.png",
                List.of(Faculty.HUMANITIES_ARTS_SOCIAL_SCIENCES),
                List.of("ENGL1500", "ENGL1800", "WRIT1001", "WRIT2250", "COMU1052"),
                List.<String[]>of(
                        new String[]{"Narrative", "Teaches writing as storytelling"},
                        new String[]{"Encouraging", "Creates a safe space to find your voice"},
                        new String[]{"Structured", "Clear frameworks for essays and creative pieces"}
                ),
                List.<String[]>of(
                        new String[]{"13 Grammy Awards", "Recording Academy", "2024"}
                )
        );

        createDemoTutor(
                "Elon", "Musk", "elon.musk.demo@tutorlink.com", "elonmusk",
                "Brisbane City QLD, Australia", -27.4705, 153.0260, false, 100,
                "Will question your entire project scope and then help you rebuild it faster. May suggest adding rockets.",
                "When something is important enough, you do it even if the odds are not in your favour.",
                "profile-images/elon-musk.webp",
                List.of(Faculty.ENGINEERING_ARCHITECTURE_IT, Faculty.BUSINESS_ECONOMICS_LAW),
                List.of("CSSE1001", "CSSE2002", "ENGG1100", "ENGG1700", "MECH2100"),
                List.<String[]>of(
                        new String[]{"First Principles", "Strips problems down to their core"},
                        new String[]{"Ambitious", "Pushes you to think bigger than you thought possible"},
                        new String[]{"Iterative", "Build fast, break things, improve"}
                ),
                List.<String[]>of(
                        new String[]{"BSc Economics", "University of Pennsylvania", "1997"},
                        new String[]{"BSc Physics", "University of Pennsylvania", "1997"}
                )
        );

        createDemoTutor(
                "Steve", "Irwin", "steve.irwin.demo@tutorlink.com", "steveirwin",
                "Woolloongabba QLD, Australia", -27.4930, 153.0360, false, 65,
                "Crikey! Makes biology exciting and easy to understand. High energy, great explanations.",
                "Crikey, mate. You're in for one heck of a lesson.",
                "profile-images/steve-irwin.jpg",
                List.of(Faculty.SCIENCE, Faculty.HEALTH_MEDICINE_BEHAVIOURAL_SCIENCES),
                List.of("BIOL1020", "BIOL1030", "BIOL2200", "ERTH1000", "ANAT1005"),
                List.<String[]>of(
                        new String[]{"Passionate", "Genuine love for the subject is contagious"},
                        new String[]{"Hands-on", "Gets you out of the classroom and into the world"},
                        new String[]{"Energetic", "No student falls asleep in these sessions"}
                ),
                List.<String[]>of(
                        new String[]{"Certificate in Wildlife Management", "Queensland TAFE", "1988"},
                        new String[]{"Honorary Doctorate", "University of Queensland", "2004"}
                )
        );

        createDemoTutor(
                "Ada", "Lovelace", "ada.lovelace.demo@tutorlink.com", "adalovelace",
                "Kelvin Grove QLD, Australia", -27.4480, 153.0130, true, 78,
                "Explains algorithms clearly and elegantly. Great for students struggling with programming logic.",
                "The more I study, the more insatiable do I feel my genius for it to be.",
                "profile-images/ada-lovelace.png",
                List.of(Faculty.ENGINEERING_ARCHITECTURE_IT, Faculty.SCIENCE),
                List.of("CSSE1001", "CSSE2002", "CSSE2310", "COMP2048", "MATH1061", "INFS1200"),
                List.<String[]>of(
                        new String[]{"Logical", "Builds understanding step by step"},
                        new String[]{"Elegant", "Teaches clean, readable solutions"},
                        new String[]{"Analytical", "Develops your ability to break down any problem"}
                ),
                List.<String[]>of(
                        new String[]{"Mathematics Tutoring", "University of London", "1840"},
                        new String[]{"First Algorithm", "Analytical Engine Notes", "1843"}
                )
        );
    }

    private void createDemoTutor(
            String firstname, String lastname, String email, String username,
            String location, Double latitude, Double longitude,
            boolean remote, Integer hourlyRate, String bio, String tagline,
            String profileImageKey, List<Faculty> faculties,
            List<String> courseCodes, List<String[]> styles, List<String[]> credentials
    ) {
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setEnabled(true);
        user.setRoles(Set.of(Role.STUDENT, Role.TUTOR));
        User savedUser = userRepository.save(user);

        Tutor tutor = new Tutor();
        tutor.setUser(savedUser);
        tutor.setLocation(location);
        tutor.setLatitude(latitude);
        tutor.setLongitude(longitude);
        tutor.setRemote(remote);
        tutor.setHourlyRate(hourlyRate);
        tutor.setBio(bio);
        tutor.setTagline(tagline);
        tutor.setProfileImageKey(profileImageKey);
        tutor.getFaculties().addAll(faculties);

        courseCodes.forEach(code ->
                courseRepository.findByCourseCode(code)
                        .ifPresent(tutor.getCourses()::add)
        );

        styles.forEach(s -> {
            TutorStyle style = new TutorStyle();
            style.setTutor(tutor);
            style.setLabel(s[0]);
            style.setDescription(s[1]);
            tutor.getStyles().add(style);
        });

        credentials.forEach(c -> {
            TutorCredential cred = new TutorCredential();
            cred.setTutor(tutor);
            cred.setTitle(c[0]);
            cred.setInstitution(c[1]);
            cred.setYear(Integer.parseInt(c[2]));
            tutor.getCredentials().add(cred);
        });

        tutorRepository.save(tutor);
    }
}
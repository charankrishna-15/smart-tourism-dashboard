package com.tourism.service;

import com.tourism.db.DestinationDAO;
import com.tourism.db.SeasonalDataDAO;
import com.tourism.db.TouristDAO;
import com.tourism.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * DataSeeder — populates the database with realistic sample data
 * for destinations, tourists, and seasonal visitor history.
 */
public class DataSeeder {
        private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

        private final DestinationDAO destinationDAO;
        private final TouristDAO touristDAO;
        private final SeasonalDataDAO seasonalDataDAO;

        public DataSeeder() {
                this.destinationDAO = new DestinationDAO();
                this.touristDAO = new TouristDAO();
                this.seasonalDataDAO = new SeasonalDataDAO();
        }

        public void seed() throws SQLException {
                // Only seed if empty
                if (!destinationDAO.findAll().isEmpty()) {
                        logger.info("Database already seeded. Skipping.");
                        return;
                }
                logger.info("Seeding database with sample data...");
                seedDestinations();
                seedTourists();
                seedSeasonalData();
                logger.info("Database seeding complete.");
        }

        private void seedDestinations() throws SQLException {
                Destination[] dests = {
                                new Destination(0, "Taj Mahal, Agra", "India", "temperate", "cultural", 4.9, 15000,
                                                3600,
                                                10, 3,
                                                27.1751, 78.0421,
                                                "Iconic white marble mausoleum and symbol of love.",
                                                "sightseeing,photography,history tours"),
                                new Destination(0, "Goa", "India", "tropical", "relaxation", 4.7, 20000, 7200, 11, 2,
                                                15.2993, 74.1240,
                                                "Famous for its pristine beaches, vibrant nightlife, and Portuguese heritage.",
                                                "beaches,water sports,nightlife,churches"),
                                new Destination(0, "Jaipur", "India", "arid", "cultural", 4.8, 18000, 5400, 10, 3,
                                                26.9124, 75.7873,
                                                "The Pink City, known for its historic palaces and forts.",
                                                "forts,palaces,shopping,cultural tours"),
                                new Destination(0, "Kerala Backwaters", "India", "tropical", "relaxation", 4.8, 25000,
                                                5400, 9, 3,
                                                9.4981, 76.3388,
                                                "Serene network of lagoons and canals with houseboats.",
                                                "houseboats,ayurveda,nature walks"),
                                new Destination(0, "Leh Ladakh", "India", "arctic", "adventure", 4.9, 30000, 9000, 5, 9,
                                                34.1526, 77.5771,
                                                "High-altitude desert with stunning landscapes and Buddhist monasteries.",
                                                "trekking,biking,monasteries,camping"),
                                new Destination(0, "Rishikesh", "India", "temperate", "adventure", 4.6, 12000, 4500, 9,
                                                5,
                                                30.0869, 78.2676,
                                                "Yoga capital of the world and hub for white-water rafting.",
                                                "yoga,river rafting,temples,bungee jumping"),
                                new Destination(0, "Varanasi", "India", "temperate", "cultural", 4.7, 10000, 3600, 10,
                                                3,
                                                25.3176, 82.9739,
                                                "One of the oldest living cities, spiritual heart of India.",
                                                "ghats,temples,boat ride,ganga aarti"),
                                new Destination(0, "Darjeeling", "India", "temperate", "relaxation", 4.6, 18000, 5400,
                                                3,
                                                5,
                                                27.0360, 88.2627,
                                                "Scenic hill station known for its tea gardens and toy train.",
                                                "tea gardens,toy train,mountain views"),
                                new Destination(0, "Andaman Islands", "India", "tropical", "relaxation", 4.8, 40000,
                                                9000, 10, 5,
                                                11.7401, 92.6586,
                                                "Archipelago with pristine beaches, coral reefs, and water sports.",
                                                "scuba diving,snorkeling,beaches,cellular jail"),
                                new Destination(0, "Hampi", "India", "arid", "cultural", 4.7, 15000, 4500, 10, 3,
                                                15.3350, 76.4600,
                                                "UNESCO World Heritage site with ancient ruins and temples.",
                                                "temples,ruins,bouldering,history"),
                                new Destination(0, "Munnar", "India", "tropical", "relaxation", 4.7, 20000, 5400, 9, 5,
                                                10.0889, 77.0595,
                                                "Idyllic hill station with rolling tea plantations.",
                                                "tea gardens,trekking,waterfalls,wildlife"),
                                new Destination(0, "Udaipur", "India", "arid", "cultural", 4.8, 22000, 5400, 10, 3,
                                                24.5854, 73.7125,
                                                "City of Lakes, known for its elegant palaces and romantic setting.",
                                                "palaces,boat ride,lakes,cultural shows"),
                                new Destination(0, "Manali", "India", "temperate", "adventure", 4.7, 18000, 7200, 3, 6,
                                                32.2396, 77.1887,
                                                "Popular hill station and gateway for skiing and trekking.",
                                                "skiing,trekking,temples,paragliding"),
                                new Destination(0, "Ranthambore", "India", "arid", "adventure", 4.6, 25000, 4500, 10, 4,
                                                26.0173, 76.2253,
                                                "Among the best national parks to spot Bengal tigers.",
                                                "wildlife safari,tiger spotting,fort,nature photography"),
                                new Destination(0, "Mysore", "India", "tropical", "cultural", 4.6, 12000, 3600, 10, 2,
                                                12.2958, 76.6394,
                                                "City of palaces with a rich royal heritage.",
                                                "palaces,silk weaving,gardens,temples"),
                                new Destination(0, "Srinagar", "India", "temperate", "relaxation", 4.8, 25000, 7200, 4,
                                                10,
                                                34.0837, 74.7973,
                                                "Summer capital of J&K with beautiful Dal Lake.",
                                                "shikara ride,gardens,houseboat,shopping"),
                                new Destination(0, "Kanyakumari", "India", "tropical", "cultural", 4.5, 12000, 3600, 10,
                                                3,
                                                8.0883, 77.5385,
                                                "The southernmost tip of India where three oceans meet.",
                                                "vivekananda rock,sunset,temples,beaches"),
                                new Destination(0, "Kaziranga", "India", "tropical", "adventure", 4.7, 20000, 5400, 11,
                                                4,
                                                26.5775, 93.1711,
                                                "National park famous for the one-horned rhinoceros.",
                                                "wildlife safari,elephant ride,bird watching"),
                                new Destination(0, "Pushkar", "India", "arid", "cultural", 4.5, 10000, 3600, 10, 3,
                                                26.4905, 74.5504,
                                                "Holy city known for its Brahma temple and camel fair.",
                                                "temple,lake,camel ride,shopping"),
                                new Destination(0, "Sundarbans", "India", "tropical", "adventure", 4.6, 15000, 5400, 10,
                                                3,
                                                21.9497, 89.1833,
                                                "World's largest mangrove forest and home to Royal Bengal Tigers.",
                                                "boat safari,wildlife,mangrove forest,bird watching"),
                                new Destination(0, "Ooty", "India", "temperate", "relaxation", 4.6, 14000, 4500, 3, 6,
                                                11.4064, 76.6932,
                                                "Beautiful hill station famous for its tea gardens and toy train.",
                                                "botanical gardens,toy train,lake"),
                                new Destination(0, "Shimla", "India", "temperate", "relaxation", 4.7, 22000, 5400, 3, 6,
                                                31.1048, 77.1734,
                                                "Capital of Himachal Pradesh with colonial architecture.",
                                                "mall road,ridge,toy train"),
                                new Destination(0, "Jaisalmer", "India", "arid", "adventure", 4.8, 16000, 6000, 10, 3,
                                                26.9157, 70.9083,
                                                "The Golden City located in the heart of the Thar Desert.",
                                                "desert safari,fort,camping"),
                                new Destination(0, "Amritsar", "India", "temperate", "cultural", 4.9, 25000, 3600, 10,
                                                3,
                                                31.6340, 74.8723, "Home to the glorious Golden Temple.",
                                                "golden temple,wagah border,food"),
                                new Destination(0, "Pondicherry", "India", "tropical", "relaxation", 4.6, 18000, 4800,
                                                10, 3, 11.9416, 79.8083,
                                                "A former French colony with beautiful promenade and beaches.",
                                                "french quarter,beaches,auroville"),
                                new Destination(0, "Mahabaleshwar", "India", "temperate", "relaxation", 4.5, 12000,
                                                4500,
                                                10, 5, 17.9307, 73.6477,
                                                "Hill station renowned for its captivating beauty and strawberries.",
                                                "viewpoints,strawberries,lake"),
                                new Destination(0, "Coorg", "India", "tropical", "relaxation", 4.7, 19000, 5400, 10, 4,
                                                12.3375, 75.8069,
                                                "The Scotland of India, known for its coffee estates.",
                                                "coffee plantations,waterfalls,trekking"),
                                new Destination(0, "Spiti Valley", "India", "arctic", "adventure", 4.8, 10000, 7500, 5,
                                                10, 32.2396, 78.0349,
                                                "Cold desert mountain valley located high in the Himalayas.",
                                                "road trip,monasteries,camping"),
                                new Destination(0, "Auli", "India", "arctic", "adventure", 4.7, 8000, 6600, 12, 3,
                                                30.5333, 79.5667,
                                                "Premier ski resort destination in the Garhwal Himalayas.",
                                                "skiing,cable car,trekking"),
                                new Destination(0, "Khajuraho", "India", "arid", "cultural", 4.8, 11000, 4200, 10, 3,
                                                24.8318, 79.9199,
                                                "Famous for its stunning temples with intricate carvings.",
                                                "temples,heritage walks,light show")
                };
                for (Destination d : dests) {
                        destinationDAO.insert(d);
                }
                logger.info("Seeded {} destinations.", dests.length);
        }

        private void seedTourists() throws SQLException {
                Tourist[] tourists = {
                                new Tourist(0, "Charan", "charan@example.com", "tropical", "relaxation", 100000, 14),
                                new Tourist(0, "Jahnavi", "jahnavi@example.com", "temperate", "adventure", 240000, 21),
                                new Tourist(0, "Deekshitha", "deekshitha@example.com", "arid", "cultural", 150000, 10),
                                new Tourist(0, "Anil", "anil@example.com", "tropical", "adventure", 180000, 18),
                                new Tourist(0, "Dinesh", "dinesh@example.com", "temperate", "cultural", 270000, 28),
                                new Tourist(0, "Irshad", "irshad@example.com", "arctic", "adventure", 225000, 14),
                                new Tourist(0, "Harsha", "harsha@example.com", "tropical", "cultural", 120000, 12),
                                new Tourist(0, "Shankar", "shankar@example.com", "temperate", "relaxation", 195000, 15),
                                new Tourist(0, "Abhinay", "abhinay@example.com", "arid", "adventure", 165000, 20),
                                new Tourist(0, "Rafi", "rafi@example.com", "arctic", "cultural", 255000, 25),
                };
                for (Tourist t : tourists) {
                        touristDAO.insert(t);
                }
                logger.info("Seeded {} tourists.", tourists.length);
        }

        private void seedSeasonalData() throws SQLException {
                // Monthly visitor data for 3 years (2022-2024) for select destinations
                // Format: [destId, year, month, visitors, temp, precipitation]
                int id = 0;
                // Taj Mahal (id=1)
                int[][] tajData = {
                                { 1, 2022, 1, 18000, 15, 10 }, { 1, 2022, 2, 16000, 20, 15 },
                                { 1, 2022, 3, 22000, 28, 10 },
                                { 1, 2022, 4, 15000, 35, 5 }, { 1, 2022, 5, 10000, 42, 5 },
                                { 1, 2022, 6, 8000, 40, 50 },
                                { 1, 2022, 7, 12000, 35, 120 }, { 1, 2022, 8, 15000, 33, 100 },
                                { 1, 2022, 9, 18000, 34, 40 },
                                { 1, 2022, 10, 35000, 32, 10 }, { 1, 2022, 11, 45000, 25, 5 },
                                { 1, 2022, 12, 50000, 18, 5 },
                                { 1, 2023, 1, 19000, 15, 10 }, { 1, 2023, 2, 17000, 20, 15 },
                                { 1, 2023, 3, 24000, 28, 10 },
                                { 1, 2023, 4, 16000, 35, 5 }, { 1, 2023, 5, 11000, 42, 5 },
                                { 1, 2023, 6, 9000, 40, 50 },
                                { 1, 2023, 7, 13000, 35, 120 }, { 1, 2023, 8, 16000, 33, 100 },
                                { 1, 2023, 9, 19000, 34, 40 },
                                { 1, 2023, 10, 37000, 32, 10 }, { 1, 2023, 11, 47000, 25, 5 },
                                { 1, 2023, 12, 52000, 18, 5 },
                                { 1, 2024, 1, 21000, 15, 10 }, { 1, 2024, 2, 19000, 20, 15 },
                                { 1, 2024, 3, 26000, 28, 10 },
                                { 1, 2024, 4, 17000, 35, 5 }, { 1, 2024, 5, 12000, 42, 5 },
                                { 1, 2024, 6, 10000, 40, 50 },
                                { 1, 2024, 7, 14000, 35, 120 }, { 1, 2024, 8, 17000, 33, 100 },
                                { 1, 2024, 9, 21000, 34, 40 },
                                { 1, 2024, 10, 39000, 32, 10 }, { 1, 2024, 11, 49000, 25, 5 },
                                { 1, 2024, 12, 54000, 18, 5 },
                };
                // Goa (id=2)
                int[][] goaData = {
                                { 2, 2022, 1, 40000, 28, 5 }, { 2, 2022, 2, 35000, 29, 5 },
                                { 2, 2022, 3, 25000, 32, 5 },
                                { 2, 2022, 4, 15000, 33, 15 }, { 2, 2022, 5, 10000, 34, 80 },
                                { 2, 2022, 6, 8000, 30, 800 },
                                { 2, 2022, 7, 5000, 29, 900 }, { 2, 2022, 8, 6000, 29, 500 },
                                { 2, 2022, 9, 12000, 30, 250 },
                                { 2, 2022, 10, 25000, 31, 100 }, { 2, 2022, 11, 45000, 30, 30 },
                                { 2, 2022, 12, 55000, 29, 5 },
                                { 2, 2023, 1, 42000, 28, 5 }, { 2, 2023, 2, 37000, 29, 5 },
                                { 2, 2023, 3, 27000, 32, 5 },
                                { 2, 2023, 4, 17000, 33, 15 }, { 2, 2023, 5, 12000, 34, 80 },
                                { 2, 2023, 6, 9000, 30, 800 },
                                { 2, 2023, 7, 6000, 29, 900 }, { 2, 2023, 8, 7000, 29, 500 },
                                { 2, 2023, 9, 14000, 30, 250 },
                                { 2, 2023, 10, 27000, 31, 100 }, { 2, 2023, 11, 47000, 30, 30 },
                                { 2, 2023, 12, 58000, 29, 5 },
                                { 2, 2024, 1, 45000, 28, 5 }, { 2, 2024, 2, 40000, 29, 5 },
                                { 2, 2024, 3, 30000, 32, 5 },
                                { 2, 2024, 4, 19000, 33, 15 }, { 2, 2024, 5, 14000, 34, 80 },
                                { 2, 2024, 6, 10000, 30, 800 },
                                { 2, 2024, 7, 7000, 29, 900 }, { 2, 2024, 8, 8000, 29, 500 },
                                { 2, 2024, 9, 16000, 30, 250 },
                                { 2, 2024, 10, 30000, 31, 100 }, { 2, 2024, 11, 50000, 30, 30 },
                                { 2, 2024, 12, 60000, 29, 5 },
                };
                // Leh Ladakh (id=5)
                int[][] ladakhData = {
                                { 5, 2022, 1, 1000, -10, 20 }, { 5, 2022, 2, 1500, -8, 20 },
                                { 5, 2022, 3, 3000, -2, 15 },
                                { 5, 2022, 4, 8000, 5, 10 }, { 5, 2022, 5, 18000, 10, 10 },
                                { 5, 2022, 6, 30000, 15, 10 },
                                { 5, 2022, 7, 35000, 18, 15 }, { 5, 2022, 8, 30000, 17, 15 },
                                { 5, 2022, 9, 20000, 12, 10 },
                                { 5, 2022, 10, 10000, 5, 10 }, { 5, 2022, 11, 3000, -2, 15 },
                                { 5, 2022, 12, 1500, -8, 20 },
                                { 5, 2023, 1, 1200, -10, 20 }, { 5, 2023, 2, 1700, -8, 20 },
                                { 5, 2023, 3, 3500, -2, 15 },
                                { 5, 2023, 4, 9000, 5, 10 }, { 5, 2023, 5, 20000, 10, 10 },
                                { 5, 2023, 6, 32000, 15, 10 },
                                { 5, 2023, 7, 38000, 18, 15 }, { 5, 2023, 8, 32000, 17, 15 },
                                { 5, 2023, 9, 22000, 12, 10 },
                                { 5, 2023, 10, 12000, 5, 10 }, { 5, 2023, 11, 3500, -2, 15 },
                                { 5, 2023, 12, 1800, -8, 20 },
                                { 5, 2024, 1, 1500, -10, 20 }, { 5, 2024, 2, 2000, -8, 20 },
                                { 5, 2024, 3, 4000, -2, 15 },
                                { 5, 2024, 4, 10000, 5, 10 }, { 5, 2024, 5, 22000, 10, 10 },
                                { 5, 2024, 6, 35000, 15, 10 },
                                { 5, 2024, 7, 42000, 18, 15 }, { 5, 2024, 8, 35000, 17, 15 },
                                { 5, 2024, 9, 25000, 12, 10 },
                                { 5, 2024, 10, 14000, 5, 10 }, { 5, 2024, 11, 4000, -2, 15 },
                                { 5, 2024, 12, 2000, -8, 20 },
                };

                int sdId = 1;
                for (int[] row : tajData) {
                        seasonalDataDAO.insert(
                                        new SeasonalData(0, row[0], "Taj Mahal, Agra", row[1], row[2], row[3], row[4],
                                                        row[5]));
                }
                for (int[] row : goaData) {
                        seasonalDataDAO.insert(
                                        new SeasonalData(0, row[0], "Goa", row[1], row[2], row[3], row[4], row[5]));
                }
                for (int[] row : ladakhData) {
                        seasonalDataDAO.insert(new SeasonalData(0, row[0], "Leh Ladakh", row[1], row[2], row[3], row[4],
                                        row[5]));
                }
                logger.info("Seeded seasonal data for 3 Indian destinations × 3 years.");
        }
}

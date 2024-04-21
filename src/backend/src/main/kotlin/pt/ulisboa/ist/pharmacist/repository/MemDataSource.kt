package pt.ulisboa.ist.pharmacist.repository

import java.util.concurrent.atomic.AtomicLong
import org.springframework.stereotype.Component
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User

@Component
class MemDataSource {

    val pharmacies = mutableMapOf<Long, Pharmacy>()
    val medicines = mutableMapOf<Long, Medicine>()

    val users = mutableMapOf<String, User>()
    val accessTokens = mutableListOf<AccessToken>()

    val pharmaciesCounter = AtomicLong(0)
    val medicinesCounter = AtomicLong(0)

    init {

        medicines += mapOf(
            0L to Medicine(
                0,
                "Ranitidine",
                "Antacid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            1L to Medicine(
                1,
                "Paracetamol",
                "Painkiller",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            2L to Medicine(
                2,
                "Ibuprofen",
                "Painkiller",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            3L to Medicine(
                3,
                "Aspirin",
                "Painkiller",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            4L to Medicine(
                4,
                "Diazepam",
                "Anxiolytic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            5L to Medicine(
                5,
                "Lorazepam",
                "Anxiolytic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            6L to Medicine(
                6,
                "Alprazolam",
                "Anxiolytic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            7L to Medicine(
                7,
                "Citalopram",
                "Antidepressant",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            8L to Medicine(
                8,
                "Sertraline",
                "Antidepressant",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            9L to Medicine(
                9,
                "Fluoxetine",
                "Antidepressant",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            10L to Medicine(
                10,
                "Omeprazole",
                "Antacid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            11L to Medicine(
                11,
                "Lansoprazole",
                "Antacid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            12L to Medicine(
                12,
                "Pantoprazole",
                "Antacid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            13L to Medicine(
                13,
                "Simvastatin",
                "Anticholesterol",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            14L to Medicine(
                14,
                "Atorvastatin",
                "Anticholesterol",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            15L to Medicine(
                15,
                "Rosuvastatin",
                "Anticholesterol",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            16L to Medicine(
                16,
                "Metformin",
                "Antidiabetic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            17L to Medicine(
                17,
                "Gliclazide",
                "Antidiabetic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            18L to Medicine(
                18,
                "Sitagliptin",
                "Antidiabetic",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            19L to Medicine(
                19,
                "Levothyroxine",
                "Thyroid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            20L to Medicine(
                20,
                "Liothyronine",
                "Thyroid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            21L to Medicine(
                21,
                "Carbimazole",
                "Thyroid",
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
        )
        medicinesCounter.set(medicines.size.toLong())

        pharmacies += mapOf(
            0L to Pharmacy(
                0,
                "Farmácia São João",
                Location(38.736946, -9.133621),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png",
                medicines = mutableListOf(
                    MedicineStock(medicines[0L]!!, 100),
                    MedicineStock(medicines[1L]!!, 50),
                    MedicineStock(medicines[2L]!!, 40),
                )
            ),
            1L to Pharmacy(
                1,
                "Farmácia do Chiado",
                Location(38.7106, -9.1401),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png",
                medicines = mutableListOf(
                    MedicineStock(medicines[3L]!!, 100),
                    MedicineStock(medicines[4L]!!, 50),
                    MedicineStock(medicines[5L]!!, 40),
                )
            ),
            2L to Pharmacy(
                2,
                "Farmácia do Rossio",
                Location(38.7149, -9.1394),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            3L to Pharmacy(
                3,
                "Farmácia do Restelo",
                Location(38.7014, -9.2094),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            4L to Pharmacy(
                4,
                "Farmácia do Areeiro",
                Location(38.7425, -9.1321),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            5L to Pharmacy(
                5,
                "Farmácia do Lumiar",
                Location(38.7706, -9.1601),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            ),
            6L to Pharmacy(
                6,
                "Farmácia do Parque",
                Location(38.7606, -9.1501),
                "https://cdn.aerohealthcare.com/wp-content/uploads/2023/01/HV20G.png"
            )
        )
        pharmaciesCounter.set(pharmacies.size.toLong())

    }
}
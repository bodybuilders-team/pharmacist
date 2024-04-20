package pt.ulisboa.ist.pharmacist.repository

import org.springframework.stereotype.Component
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import java.util.concurrent.atomic.AtomicLong

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


    }
}
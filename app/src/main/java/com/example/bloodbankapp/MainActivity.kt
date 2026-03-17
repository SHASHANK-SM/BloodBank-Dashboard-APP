package com.example.bloodbankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

// Data class to store requests
data class BloodRequest(
    val name: String,
    val group: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloodBankApp()
        }
    }
}

@Composable
fun BloodBankApp() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFD32F2F)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {

    var requestList by remember { mutableStateOf(listOf<BloodRequest>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "🩸 Blood Bank Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        DonorCard()

        Spacer(modifier = Modifier.height(20.dp))

        BloodAvailability()

        Spacer(modifier = Modifier.height(20.dp))

        RequestSection(
            onSubmit = { newRequest ->
                requestList = requestList + newRequest
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        RequestHistory(requestList)
    }
}

@Composable
fun DonorCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Donor Name: Shashank")
            Text(text = "Blood Group: A+")
            Text(text = "Location: Bengaluru")
            Text(text = "Contact: 935304****")
        }
    }
}

@Composable
fun BloodAvailability() {
    Column {

        Text(
            text = "Available Blood Units",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        BloodRow("A+", 10)
        BloodRow("B+", 5)
        BloodRow("O+", 8)
        BloodRow("AB+", 3)
        BloodRow("A-", 2)
    }
}

@Composable
fun BloodRow(group: String, units: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = group)
            Text(text = "$units units")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestSection(onSubmit: (BloodRequest) -> Unit) {

    var patientName by remember { mutableStateOf("") }
    var selectedBloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column {

        Text(
            text = "Request Blood",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = patientName,
            onValueChange = { patientName = it },
            label = { Text("Patient Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedBloodGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Blood Group") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                bloodGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            selectedBloodGroup = group
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                scope.launch {
                    if (patientName.isBlank() || selectedBloodGroup.isBlank()) {
                        snackbarHostState.showSnackbar("Please fill all fields!")
                    } else {
                        onSubmit(BloodRequest(patientName, selectedBloodGroup))
                        snackbarHostState.showSnackbar("Request Added!")

                        patientName = ""
                        selectedBloodGroup = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Request")
        }

        Spacer(modifier = Modifier.height(10.dp))

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun RequestHistory(requests: List<BloodRequest>) {

    Text(
        text = "Request History",
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(10.dp))

    if (requests.isEmpty()) {
        Text("No requests yet")
    } else {
        requests.forEach { request ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${request.name}")
                    Text("Blood Group: ${request.group}")
                }
            }
        }
    }
}
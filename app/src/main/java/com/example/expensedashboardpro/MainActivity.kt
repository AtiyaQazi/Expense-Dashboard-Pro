package com.example.expensedashboardpro

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseDashboardApp()
                }
            }
        }
    }
}

// Upgraded Data Class with Date string
data class Expense(val id: Long, val description: String, val amount: Float, val category: String, val date: String)

// Persistent Storage Helpers for Expenses
fun saveExpenses(context: Context, expenses: List<Expense>) {
    val prefs = context.getSharedPreferences("ExpensePrefs", Context.MODE_PRIVATE)
    val expenseString = expenses.joinToString(separator = ";;") {
        "${it.id}|${it.description}|${it.amount}|${it.category}|${it.date}"
    }
    prefs.edit().putString("expense_data", expenseString).apply()
}

fun loadExpenses(context: Context): List<Expense> {
    val prefs = context.getSharedPreferences("ExpensePrefs", Context.MODE_PRIVATE)
    val data = prefs.getString("expense_data", "") ?: ""
    if (data.isEmpty()) return emptyList()

    return data.split(";;").mapNotNull {
        val parts = it.split("|")
        if (parts.size == 5) {
            Expense(parts[0].toLong(), parts[1], parts[2].toFloat(), parts[3], parts[4])
        } else null
    }
}

// Persistent Storage Helpers for Budget
fun saveBudget(context: Context, budget: String) {
    context.getSharedPreferences("ExpensePrefs", Context.MODE_PRIVATE)
        .edit().putString("saved_budget", budget).apply()
}

fun loadBudget(context: Context): String {
    return context.getSharedPreferences("ExpensePrefs", Context.MODE_PRIVATE)
        .getString("saved_budget", "") ?: ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDashboardApp() {
    val context = LocalContext.current
    var expenses by remember { mutableStateOf(loadExpenses(context)) }

    // User Inputs
    var amountInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf(loadBudget(context)) }
    var selectedCategory by remember { mutableStateOf("Food") }
    val categories = listOf("Food", "Transport", "University")

    // Calculations
    val totalExpense = expenses.sumOf { it.amount.toDouble() }.toFloat()
    val foodTotal = expenses.filter { it.category == "Food" }.sumOf { it.amount.toDouble() }.toFloat()
    val transportTotal = expenses.filter { it.category == "Transport" }.sumOf { it.amount.toDouble() }.toFloat()
    val uniTotal = expenses.filter { it.category == "University" }.sumOf { it.amount.toDouble() }.toFloat()

    // Dynamic Budget Logic
    val currentBudget = budgetInput.toFloatOrNull()
    val isOverBudget = currentBudget != null && totalExpense > currentBudget

    // WE REPLACED THE STATIC COLUMN WITH A FULL-PAGE LAZY COLUMN FOR SMOOTH SCROLLING
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // EVERYTHING AT THE TOP IS NOW INSIDE AN 'item' BLOCK SO IT SCROLLS WITH THE LIST
        item {
            Text(
                text = "Expense Dashboard Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            )

            // Optional Budget Input Field
            OutlinedTextField(
                value = budgetInput,
                onValueChange = {
                    budgetInput = it
                    saveBudget(context, it) // Save budget instantly when typing
                },
                label = { Text("Set Monthly Budget (Optional Rs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Spent: Rs $totalExpense",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                    if (isOverBudget) {
                        Text(text = "⚠️ Warning: You have exceeded your Rs $currentBudget budget!", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ExpenseBar(category = "Food", amount = foodTotal, total = totalExpense, color = Color(0xFFE57373))
                    ExpenseBar(category = "Transport", amount = transportTotal, total = totalExpense, color = Color(0xFF81C784))
                    ExpenseBar(category = "University", amount = uniTotal, total = totalExpense, color = Color(0xFF64B5F6))
                }
            }

            // Expense Entry Form
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    label = { Text("Expense details") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Rs") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        val amount = amountInput.toFloatOrNull()
                        if (amount != null && descInput.isNotBlank()) {
                            // Generate Current Date and Time String
                            val currentDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

                            val newExpense = Expense(
                                id = System.currentTimeMillis(),
                                description = descInput,
                                amount = amount,
                                category = selectedCategory,
                                date = currentDate // Save Date here!
                            )
                            val updatedList = expenses + newExpense
                            expenses = updatedList
                            saveExpenses(context, updatedList)
                            amountInput = ""
                            descInput = ""
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Recent Transactions", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // TRANSACTION LIST
        items(expenses.reversed()) { expense ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = expense.description, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        // Added the Date variable right next to the category!
                        Text(text = "${expense.category} • ${expense.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(text = "Rs ${expense.amount}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    IconButton(onClick = {
                        val updatedList = expenses.filter { it.id != expense.id }
                        expenses = updatedList
                        saveExpenses(context, updatedList)
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseBar(category: String, amount: Float, total: Float, color: Color) {
    val targetPercentage = if (total > 0) amount / total else 0f
    val animatedPercentage by animateFloatAsState(targetValue = targetPercentage, label = "BarAnimation")

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category, fontSize = 14.sp)
            Text(text = "Rs $amount", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.LightGray.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPercentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
        }
    }
}
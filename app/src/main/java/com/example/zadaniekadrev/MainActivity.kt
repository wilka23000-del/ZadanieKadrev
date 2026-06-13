package com.example.zadaniekadrev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zadaniekadrev.data.SettingsManager
import com.example.zadaniekadrev.data.ShoppingItem
import com.example.zadaniekadrev.ui.theme.ZadanieKadrevTheme
import com.example.zadaniekadrev.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context.applicationContext) }
            val viewModel: MainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return MainViewModel(settingsManager) as T
                    }
                }
            )

            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val items by viewModel.items.collectAsState()

            ZadanieKadrevTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShoppingListScreen(
                        items = items,
                        isDark = isDarkTheme,
                        onAdd = { viewModel.addItem(it) },
                        onRemove = { viewModel.removeItem(it) },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onToggleChecked = { viewModel.toggleItemChecked(it) },
                        onUpdateQuantity = { item, delta -> viewModel.updateQuantity(item, delta) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    items: List<ShoppingItem>,
    isDark: Boolean,
    onAdd: (String) -> Unit,
    onRemove: (ShoppingItem) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleChecked: (ShoppingItem) -> Unit,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newItemText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Список покупок",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Тема",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (items.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = items.size, key = { items[it].id }) { index ->
                        val item = items[index]
                        ShoppingItemCard(
                            item = item,
                            onRemove = { onRemove(item) },
                            onToggleChecked = { onToggleChecked(item) },
                            onUpdateQuantity = { onUpdateQuantity(item, it) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddProductDialog(
                onDismiss = { showDialog = false },
                onConfirm = { 
                    onAdd(it)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Тут пока пусто",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            "Добавьте первый товар кнопкой ниже",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    onRemove: () -> Unit,
    onToggleChecked: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (item.isChecked) 0.6f else 1f,
        label = "alpha"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onToggleChecked() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isChecked) MaterialTheme.colorScheme.tertiary 
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    .clickable { onToggleChecked() },
                contentAlignment = Alignment.Center
            ) {
                if (item.isChecked) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = Color.White)
                }
            }
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = if (item.isChecked) 0.2f else 0.5f
                        )
                    )
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { onUpdateQuantity(-1) },
                    modifier = Modifier.size(32.dp),
                    enabled = !item.isChecked
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = item.quantity.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = { onUpdateQuantity(1) },
                    modifier = Modifier.size(32.dp),
                    enabled = !item.isChecked
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Что добавим?", fontWeight = FontWeight.Black) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Название товара") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Добавить", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

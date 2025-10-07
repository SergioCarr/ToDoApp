package com.example.todoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.ui.theme.ToDoAppTheme
import kotlinx.coroutines.launch
import com.example.todoapp.ui.theme.DarkBlue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme(dynamicColor = false) {
                val tasks = remember{
                    mutableStateListOf(
                        Task(1,"Go to the gym",false),
                        Task(2,"Go to the store",false),
                        Task(3,"Go to the park",false),

                    )
                }

                MainAppScreen(tasks = tasks,onTaskCheckedChange = { task, isChecked ->

                    val index = tasks.indexOf(task)
                    if(index != -1) {
                        tasks[index] = task.copy(isCompleted = isChecked)
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(tasks: List<Task>, onTaskCheckedChange: (Task, Boolean) -> Unit) {
    //State and scope to control the drawer (open/close)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = {Icon(Icons.Default.Home, contentDescription = "Home")},
                    label = {Text("Home")},
                    selected = true,
                    onClick = {
                        scope.launch {drawerState.close()}
                        Toast.makeText(context,"Home Clicked", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = {Icon(Icons.Default.Settings, contentDescription = "Settings")},
                    label = {Text("Settings")},
                    selected = false,
                    onClick = {
                        scope.launch {drawerState.close()}
                        Toast.makeText(context,"Settings Clicked", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        TaskScreen(
            tasks = tasks,
            onTaskCheckedChange = onTaskCheckedChange,
            onMenuClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    tasks: List<Task>,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar={
            TopAppBar(
                title = {Text("My To-Do List")},
                navigationIcon = {
                    IconButton(onClick = onMenuClick ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Navigation Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick ={
                Toast.makeText(context,"Adding new task...",Toast.LENGTH_SHORT).show()
                },
                containerColor = DarkBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add,contentDescription="Add Task")
            }
        }
    ){
        innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(tasks){
                task->
                TaskItem(task = task, onCheckedChange = {isChecked ->
                    onTaskCheckedChange(task,isChecked)
                })
            }
        }
    }
}

@Composable
fun TaskItem(task:Task, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Text(
                text = task.title,
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
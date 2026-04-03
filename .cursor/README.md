# 🚀 PokeManiac AI Quick Start

Welcome! This file helps you get started with the PokeManiac Cursor AI configuration.

## ⚡ Quick Links

### 🎯 I want to...

| Goal | Read This | Time |
|------|-----------|------|
| **Understand the project** | [pokemaniac-guide.mdc](.cursor/rules/pokemaniac-guide.mdc) | 10 min |
| **Add a new feature** | [pokemaniac-new-feature Skill](.cursor/skills/pokemaniac-new-feature/SKILL.md) | 15 min |
| **Add API/database functionality** | [pokemaniac-data-layer Skill](.cursor/skills/pokemaniac-data-layer/SKILL.md) | 20 min |
| **Write tests** | [pokemaniac-testing Skill](.cursor/skills/pokemaniac-testing/SKILL.md) | 15 min |
| **Create a ViewModel** | [viewmodel-patterns.mdc](.cursor/rules/viewmodel-patterns.mdc) | 10 min |
| **Build Compose UI** | [compose-patterns.mdc](.cursor/rules/compose-patterns.mdc) | 10 min |
| **Follow Kotlin standards** | [kotlin-android-standards.mdc](.cursor/rules/kotlin-android-standards.mdc) | 15 min |
| **Check my work** | [development-checklists.mdc](.cursor/rules/development-checklists.mdc) | 5 min |
| **Full documentation** | [PROJECT_DOCUMENTATION.md](.cursor/PROJECT_DOCUMENTATION.md) | 20 min |

## 📚 File Organization

```
.cursor/
├── rules/                    ← Auto-apply guidelines
│   ├── pokemaniac-guide.mdc (start here!)
│   ├── pokemaniac-architecture.mdc
│   ├── viewmodel-patterns.mdc
│   ├── compose-patterns.mdc
│   ├── kotlin-android-standards.mdc
│   └── development-checklists.mdc
│
├── skills/                   ← Step-by-step guides
│   ├── pokemaniac-new-feature/
│   ├── pokemaniac-data-layer/
│   └── pokemaniac-testing/
│
└── PROJECT_DOCUMENTATION.md  ← Full reference
```

## 🎓 Learning Path

**New to the project?** Follow this path:

1. **Read:** [pokemaniac-guide.mdc](.cursor/rules/pokemaniac-guide.mdc) - 10 minutes
   - Understand module structure
   - Learn golden rules
   - See file organization

2. **Pick a task** from above

3. **Read relevant documentation** (5-15 minutes)

4. **Ask the AI** to help implement
   - Rules apply automatically
   - AI follows patterns

5. **Use checklist** to verify completeness

## 💡 Example Conversations

### Adding a Feature

You:
> "I want to add a new feature called 'TradeHistory' to show user trading history"

AI will:
- Read the `pokemaniac-new-feature` skill automatically
- Follow the step-by-step guide
- Generate code matching your patterns
- Create Activity, ViewModel, Screen, View, etc.

### Extending Data Layer

You:
> "Add an API endpoint to get user trading history with pagination"

AI will:
- Read the `pokemaniac-data-layer` skill automatically
- Create repository interface in domain
- Implement repository combining API + local storage
- Set up Retrofit service
- Create Room DAO & entity
- Register in Koin

### Writing Tests

You:
> "Write unit tests for the new TradeHistoryViewModel"

AI will:
- Read the `pokemaniac-testing` skill automatically
- Create test class with mocked dependencies
- Test state transitions
- Test error handling
- Include Given/When/Then pattern

## ⚙️ How Rules Work

Rules are **auto-applied** based on file type:

| File Type | Rule Applied | Examples |
|-----------|--------------|----------|
| `*ViewModel.kt` | viewmodel-patterns | Generate StateFlow, SharedFlow patterns |
| `*.kt` (UI) | compose-patterns | Screen/View split, state collection |
| `*.kt` (all) | kotlin-android-standards | Immutability, null safety, etc. |
| Any file | pokemaniac-architecture | Dependency validation |
| Any task | pokemaniac-guide | Architecture decisions |

**You don't need to mention them** - the AI uses them automatically!

## 🛠️ How Skills Work

Skills are **step-by-step guides** for complex tasks:

When you ask to:
- "Add a new feature..." → `pokemaniac-new-feature` skill activates
- "Add API endpoint..." → `pokemaniac-data-layer` skill activates
- "Write tests for..." → `pokemaniac-testing` skill activates

The AI reads the skill and follows its workflow.

## ✨ Key Patterns

### Architecture (Golden Rule)
```
Presentation → Domain → Data
(features)    (interfaces) (implementations)
```

### State Management
```kotlin
sealed class UiState {
    data object Loading : UiState()
    data class Data(val items: List<Item>) : UiState()
    data object Empty : UiState()
}

// Private mutable, public immutable
private val _state = MutableStateFlow<UiState>()
val state: StateFlow<UiState> = _state.asStateFlow()
```

### Screen vs View
```kotlin
// Screen: collects state, handles lifecycle
@Composable
fun MyFeatureScreen(viewModel: MyFeatureViewModel) {
    val state by viewModel.state.collectAsState()
    // ... lifecycle handling ...
    MyFeatureView(state = state)  // Delegate to View
}

// View: stateless, pure UI
@Composable
fun MyFeatureView(state: UiState) {
    when (state) {
        // ... rendering ...
    }
}
```

## 🚀 Getting Started

**Right now:**
1. Read [pokemaniac-guide.mdc](.cursor/rules/pokemaniac-guide.mdc)
2. Pick a task you want to work on
3. Read the relevant documentation
4. Ask the AI to help - it will use the patterns automatically

**Next meeting:**
- Share which tasks you completed
- Discuss any patterns that need clarification
- Plan next phase (cleanup & updates)

## 📞 Questions?

- **"What's the architecture?"** → See pokemaniac-guide.mdc
- **"How do I add a feature?"** → See pokemaniac-new-feature skill
- **"What's the ViewModel pattern?"** → See viewmodel-patterns.mdc
- **"Should I use this pattern?"** → See development-checklists.mdc

---

**Ready to get started?** → [Open pokemaniac-guide.mdc](.cursor/rules/pokemaniac-guide.mdc)

**Happy coding! 🎉**

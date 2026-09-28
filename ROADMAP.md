# USpek roadmap

## Investigate: reshape the API with Kotlin context parameters

Prototype first (a scratch module or a branch), and decide only once the prototype has been tried.

Today the API is two parallel flavors over one tree:

- `uspek { }` / `"name" o { }` reach a global mutable `GlobalUSpekContext`.
- `suspek { }` / `"name" so { }` find a `USpekContext` in the `CoroutineContext` (`ucontext`), and
  fall back to the global one.

Questions for the prototype:

- Can `context(ctx: USpekContext)` on `o` / `so` replace both the global and the
  `CoroutineContext` lookup, so the tree is passed explicitly and there's no hidden state?
- Can the blocking and suspend flavors collapse into fewer functions once the context is a
  parameter, and not looked up?
- Parallel/isolated specs for free: each `uspek { }` gets its own context, instead of sharing global state.
- What's the call-site cost? The DSL must stay as terse as `"name" o { }`.
- Migration: keep the current API as a thin compat layer, or make a clean break with a version bump?

Constraints carried over from the original 2023 note (the removed `USpek2.kt`, which targeted the
now-replaced *context receivers*):

- USpek stays **micro**: no KGround dependency for this.
- Possible order: an explicit-parameter version first (mandatory parameters instead of contexts),
  then a context-parameter version that stays very similar to it.
- The prototype may double as a small demo/tutorial (video material), so keep it readable.

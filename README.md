[![Maven Central](https://img.shields.io/maven-central/v/com.rickbusarow.statik/statik-api?style=flat-square)](https://search.maven.org/search?q=com.rickbusarow.statik)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.rickbusarow.statik/statik-api?label=snapshots&server=https%3A%2F%2Foss.sonatype.org&style=flat-square)](https://oss.sonatype.org/#nexus-search;quick~com.rickbusarow.statik)
[![License](https://img.shields.io/badge/license-apache2.0-blue?style=flat-square.svg)](https://opensource.org/licenses/Apache-2.0)

> [!IMPORTANT]
> Moving soonish to https://github.com/square/statik.

Statik is a high-level [Abstract Syntax Tree] or [Abstract Semantic Graph] with three goals:

1. Provide high-level information as members directly in node elements, so that most parsing can be
   done intuitively using semantics.

   ```kotlin
   interface StatikKotlinFunction<out PARENT> : StatikFunction<PARENT> /* ... */ {

     // semantic information about the function
     override val annotations: LazySet<StatikAnnotation<*>>
     override val visibility: StatikKotlinVisibility
     override val typeParameters: LazySet<StatikKotlinTypeParameter<*>>
     override val valueParameters: LazySet<StatikKotlinValueParameter<*>>
     override val returnType: LazyDeferred<ReferenceName>

     // syntactic information about the function
     override val modifierList: ModifierList
     override val typeParameterList: TypeParameterList?
     override val valueParameterList: ValueParameterList?
     // ...

     // standard AST features
     override val text: String
     override val containingFile: StatikKotlinFile
     override val parent: PARENT
     override val children: LazySet<StatikElement>
     override val node: KtFunction
   }
   ```

2. Make Statik's published artifacts work with multiple versions of the Kotlin compiler, so that a
   Statik update does not require a simultaneous Kotlin update. This is accomplished using
   Gradle's [Feature Variants], so that Gradle automatically selects the right Statik .jar based
   upon the Kotlin version in use.
3. Resolve non-Kotlin as well, including references to Java and Android resources generated from xml.

## License

```text
Copyright (C) 2023 Rick Busarow
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
     https://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[Abstract Syntax Tree]: https://en.wikipedia.org/wiki/Abstract_syntax_tree
[Abstract Semantic Graph]: https://en.wikipedia.org/wiki/Abstract_semantic_graph
[Feature Variants]: https://docs.gradle.org/current/userguide/feature_variants.html

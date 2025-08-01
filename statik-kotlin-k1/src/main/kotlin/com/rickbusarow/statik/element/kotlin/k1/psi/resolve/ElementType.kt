/*
 * Copyright (C) 2025 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rickbusarow.statik.element.kotlin.k1.psi.resolve

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.kdoc.lexer.KDocTokens
import org.jetbrains.kotlin.kdoc.parser.KDocElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.elements.KtFileElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

/**
 * This is copy/pasted from KtLint. KtLint's version was moved
 * for 0.48.0 and again for 0.49.0. This version won't move.
 */
@Suppress("unused", "UndocumentedPublicProperty")
public object ElementType {
  public val FILE: IElementType = KtFileElementType.INSTANCE

  // KtNodeTypes
  public val CLASS: IElementType = KtNodeTypes.CLASS
  public val FUN: IElementType = KtNodeTypes.FUN
  public val PROPERTY: IElementType = KtNodeTypes.PROPERTY
  public val DESTRUCTURING_DECLARATION: IElementType = KtNodeTypes.DESTRUCTURING_DECLARATION
  public val DESTRUCTURING_DECLARATION_ENTRY: IElementType =
    KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY
  public val OBJECT_DECLARATION: IElementType = KtNodeTypes.OBJECT_DECLARATION
  public val TYPEALIAS: IElementType = KtNodeTypes.TYPEALIAS
  public val ENUM_ENTRY: IElementType = KtNodeTypes.ENUM_ENTRY
  public val CLASS_INITIALIZER: IElementType = KtNodeTypes.CLASS_INITIALIZER
  public val SCRIPT_INITIALIZER: IElementType = KtNodeTypes.SCRIPT_INITIALIZER
  public val SECONDARY_CONSTRUCTOR: IElementType = KtNodeTypes.SECONDARY_CONSTRUCTOR
  public val PRIMARY_CONSTRUCTOR: IElementType = KtNodeTypes.PRIMARY_CONSTRUCTOR
  public val TYPE_PARAMETER_LIST: IElementType = KtNodeTypes.TYPE_PARAMETER_LIST
  public val TYPE_PARAMETER: IElementType = KtNodeTypes.TYPE_PARAMETER
  public val SUPER_TYPE_LIST: IElementType = KtNodeTypes.SUPER_TYPE_LIST
  public val DELEGATED_SUPER_TYPE_ENTRY: IElementType = KtNodeTypes.DELEGATED_SUPER_TYPE_ENTRY
  public val SUPER_TYPE_CALL_ENTRY: IElementType = KtNodeTypes.SUPER_TYPE_CALL_ENTRY
  public val SUPER_TYPE_ENTRY: IElementType = KtNodeTypes.SUPER_TYPE_ENTRY
  public val PROPERTY_DELEGATE: IElementType = KtNodeTypes.PROPERTY_DELEGATE
  public val CONSTRUCTOR_CALLEE: IElementType = KtNodeTypes.CONSTRUCTOR_CALLEE
  public val VALUE_PARAMETER_LIST: IElementType = KtNodeTypes.VALUE_PARAMETER_LIST
  public val VALUE_PARAMETER: IElementType = KtNodeTypes.VALUE_PARAMETER
  public val CLASS_BODY: IElementType = KtNodeTypes.CLASS_BODY
  public val IMPORT_LIST: IElementType = KtNodeTypes.IMPORT_LIST
  public val FILE_ANNOTATION_LIST: IElementType = KtNodeTypes.FILE_ANNOTATION_LIST
  public val IMPORT_DIRECTIVE: IElementType = KtNodeTypes.IMPORT_DIRECTIVE
  public val IMPORT_ALIAS: IElementType = KtNodeTypes.IMPORT_ALIAS
  public val MODIFIER_LIST: IElementType = KtNodeTypes.MODIFIER_LIST
  public val ANNOTATION: IElementType = KtNodeTypes.ANNOTATION
  public val ANNOTATION_ENTRY: IElementType = KtNodeTypes.ANNOTATION_ENTRY
  public val ANNOTATION_TARGET: IElementType = KtNodeTypes.ANNOTATION_TARGET
  public val TYPE_ARGUMENT_LIST: IElementType = KtNodeTypes.TYPE_ARGUMENT_LIST
  public val VALUE_ARGUMENT_LIST: IElementType = KtNodeTypes.VALUE_ARGUMENT_LIST
  public val VALUE_ARGUMENT: IElementType = KtNodeTypes.VALUE_ARGUMENT
  public val LAMBDA_ARGUMENT: IElementType = KtNodeTypes.LAMBDA_ARGUMENT
  public val VALUE_ARGUMENT_NAME: IElementType = KtNodeTypes.VALUE_ARGUMENT_NAME
  public val TYPE_REFERENCE: IElementType = KtNodeTypes.TYPE_REFERENCE
  public val USER_TYPE: IElementType = KtNodeTypes.USER_TYPE
  public val DYNAMIC_TYPE: IElementType = KtNodeTypes.DYNAMIC_TYPE
  public val FUNCTION_TYPE: IElementType = KtNodeTypes.FUNCTION_TYPE
  public val FUNCTION_TYPE_RECEIVER: IElementType = KtNodeTypes.FUNCTION_TYPE_RECEIVER
  public val NULLABLE_TYPE: IElementType = KtNodeTypes.NULLABLE_TYPE
  public val TYPE_PROJECTION: IElementType = KtNodeTypes.TYPE_PROJECTION
  public val PROPERTY_ACCESSOR: IElementType = KtNodeTypes.PROPERTY_ACCESSOR
  public val INITIALIZER_LIST: IElementType = KtNodeTypes.INITIALIZER_LIST
  public val TYPE_CONSTRAINT_LIST: IElementType = KtNodeTypes.TYPE_CONSTRAINT_LIST
  public val TYPE_CONSTRAINT: IElementType = KtNodeTypes.TYPE_CONSTRAINT
  public val CONSTRUCTOR_DELEGATION_CALL: IElementType = KtNodeTypes.CONSTRUCTOR_DELEGATION_CALL
  public val CONSTRUCTOR_DELEGATION_REFERENCE: IElementType =
    KtNodeTypes.CONSTRUCTOR_DELEGATION_REFERENCE
  public val NULL: IElementType = KtNodeTypes.NULL
  public val BOOLEAN_CONSTANT: IElementType = KtNodeTypes.BOOLEAN_CONSTANT
  public val FLOAT_CONSTANT: IElementType = KtNodeTypes.FLOAT_CONSTANT
  public val CHARACTER_CONSTANT: IElementType = KtNodeTypes.CHARACTER_CONSTANT
  public val INTEGER_CONSTANT: IElementType = KtNodeTypes.INTEGER_CONSTANT
  public val STRING_TEMPLATE: IElementType = KtNodeTypes.STRING_TEMPLATE
  public val LONG_STRING_TEMPLATE_ENTRY: IElementType = KtNodeTypes.LONG_STRING_TEMPLATE_ENTRY
  public val SHORT_STRING_TEMPLATE_ENTRY: IElementType = KtNodeTypes.SHORT_STRING_TEMPLATE_ENTRY
  public val LITERAL_STRING_TEMPLATE_ENTRY: IElementType = KtNodeTypes.LITERAL_STRING_TEMPLATE_ENTRY
  public val ESCAPE_STRING_TEMPLATE_ENTRY: IElementType = KtNodeTypes.ESCAPE_STRING_TEMPLATE_ENTRY
  public val PARENTHESIZED: IElementType = KtNodeTypes.PARENTHESIZED
  public val RETURN: IElementType = KtNodeTypes.RETURN
  public val THROW: IElementType = KtNodeTypes.THROW
  public val CONTINUE: IElementType = KtNodeTypes.CONTINUE
  public val BREAK: IElementType = KtNodeTypes.BREAK
  public val IF: IElementType = KtNodeTypes.IF
  public val CONDITION: IElementType = KtNodeTypes.CONDITION
  public val THEN: IElementType = KtNodeTypes.THEN
  public val ELSE: IElementType = KtNodeTypes.ELSE
  public val TRY: IElementType = KtNodeTypes.TRY
  public val CATCH: IElementType = KtNodeTypes.CATCH
  public val FINALLY: IElementType = KtNodeTypes.FINALLY
  public val FOR: IElementType = KtNodeTypes.FOR
  public val WHILE: IElementType = KtNodeTypes.WHILE
  public val DO_WHILE: IElementType = KtNodeTypes.DO_WHILE
  public val LOOP_RANGE: IElementType = KtNodeTypes.LOOP_RANGE
  public val BODY: IElementType = KtNodeTypes.BODY
  public val BLOCK: IElementType = KtNodeTypes.BLOCK
  public val LAMBDA_EXPRESSION: IElementType = KtNodeTypes.LAMBDA_EXPRESSION
  public val FUNCTION_LITERAL: IElementType = KtNodeTypes.FUNCTION_LITERAL
  public val ANNOTATED_EXPRESSION: IElementType = KtNodeTypes.ANNOTATED_EXPRESSION
  public val REFERENCE_EXPRESSION: IElementType = KtNodeTypes.REFERENCE_EXPRESSION
  public val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: IElementType =
    KtStubElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION
  public val OPERATION_REFERENCE: IElementType = KtNodeTypes.OPERATION_REFERENCE
  public val LABEL: IElementType = KtNodeTypes.LABEL
  public val LABEL_QUALIFIER: IElementType = KtNodeTypes.LABEL_QUALIFIER
  public val THIS_EXPRESSION: IElementType = KtNodeTypes.THIS_EXPRESSION
  public val SUPER_EXPRESSION: IElementType = KtNodeTypes.SUPER_EXPRESSION
  public val BINARY_EXPRESSION: IElementType = KtNodeTypes.BINARY_EXPRESSION
  public val BINARY_WITH_TYPE: IElementType = KtNodeTypes.BINARY_WITH_TYPE
  public val IS_EXPRESSION: IElementType = KtNodeTypes.IS_EXPRESSION
  public val PREFIX_EXPRESSION: IElementType = KtNodeTypes.PREFIX_EXPRESSION
  public val POSTFIX_EXPRESSION: IElementType = KtNodeTypes.POSTFIX_EXPRESSION
  public val LABELED_EXPRESSION: IElementType = KtNodeTypes.LABELED_EXPRESSION
  public val CALL_EXPRESSION: IElementType = KtNodeTypes.CALL_EXPRESSION
  public val ARRAY_ACCESS_EXPRESSION: IElementType = KtNodeTypes.ARRAY_ACCESS_EXPRESSION
  public val INDICES: IElementType = KtNodeTypes.INDICES
  public val DOT_QUALIFIED_EXPRESSION: IElementType = KtStubElementTypes.DOT_QUALIFIED_EXPRESSION
  public val CALLABLE_REFERENCE_EXPRESSION: IElementType = KtNodeTypes.CALLABLE_REFERENCE_EXPRESSION
  public val CLASS_LITERAL_EXPRESSION: IElementType = KtNodeTypes.CLASS_LITERAL_EXPRESSION
  public val SAFE_ACCESS_EXPRESSION: IElementType = KtNodeTypes.SAFE_ACCESS_EXPRESSION
  public val OBJECT_LITERAL: IElementType = KtNodeTypes.OBJECT_LITERAL
  public val WHEN: IElementType = KtNodeTypes.WHEN
  public val WHEN_ENTRY: IElementType = KtNodeTypes.WHEN_ENTRY
  public val WHEN_CONDITION_IN_RANGE: IElementType = KtNodeTypes.WHEN_CONDITION_IN_RANGE
  public val WHEN_CONDITION_IS_PATTERN: IElementType = KtNodeTypes.WHEN_CONDITION_IS_PATTERN
  public val WHEN_CONDITION_WITH_EXPRESSION: IElementType = KtNodeTypes.WHEN_CONDITION_EXPRESSION
  public val COLLECTION_LITERAL_EXPRESSION: IElementType = KtNodeTypes.COLLECTION_LITERAL_EXPRESSION
  public val PACKAGE_DIRECTIVE: IElementType = KtNodeTypes.PACKAGE_DIRECTIVE
  public val SCRIPT: IElementType = KtNodeTypes.SCRIPT
  public val TYPE_CODE_FRAGMENT: IElementType = KtNodeTypes.TYPE_CODE_FRAGMENT
  public val EXPRESSION_CODE_FRAGMENT: IElementType = KtNodeTypes.EXPRESSION_CODE_FRAGMENT
  public val BLOCK_CODE_FRAGMENT: IElementType = KtNodeTypes.BLOCK_CODE_FRAGMENT
  public val CONTEXT_RECEIVER_LIST: IElementType = KtNodeTypes.CONTEXT_RECEIVER_LIST
  public val CONTEXT_RECEIVER: IElementType = KtNodeTypes.CONTEXT_RECEIVER

  // KtTokens
  public val EOF: IElementType = KtTokens.EOF
  public val RESERVED: IElementType = KtTokens.RESERVED
  public val BLOCK_COMMENT: IElementType = KtTokens.BLOCK_COMMENT
  public val EOL_COMMENT: IElementType = KtTokens.EOL_COMMENT
  public val SHEBANG_COMMENT: IElementType = KtTokens.SHEBANG_COMMENT
  public val WHITE_SPACE: IElementType = KtTokens.WHITE_SPACE
  public val INTEGER_LITERAL: IElementType = KtTokens.INTEGER_LITERAL
  public val FLOAT_LITERAL: IElementType = KtTokens.FLOAT_LITERAL // FLOAT_CONSTANT
  public val CHARACTER_LITERAL: IElementType = KtTokens.CHARACTER_LITERAL
  public val CLOSING_QUOTE: IElementType = KtTokens.CLOSING_QUOTE
  public val OPEN_QUOTE: IElementType = KtTokens.OPEN_QUOTE
  public val REGULAR_STRING_PART: IElementType = KtTokens.REGULAR_STRING_PART
  public val ESCAPE_SEQUENCE: IElementType = KtTokens.ESCAPE_SEQUENCE
  public val SHORT_TEMPLATE_ENTRY_START: IElementType = KtTokens.SHORT_TEMPLATE_ENTRY_START
  public val LONG_TEMPLATE_ENTRY_START: IElementType = KtTokens.LONG_TEMPLATE_ENTRY_START
  public val LONG_TEMPLATE_ENTRY_END: IElementType = KtTokens.LONG_TEMPLATE_ENTRY_END
  public val DANGLING_NEWLINE: IElementType = KtTokens.DANGLING_NEWLINE
  public val PACKAGE_KEYWORD: IElementType = KtTokens.PACKAGE_KEYWORD
  public val AS_KEYWORD: IElementType = KtTokens.AS_KEYWORD
  public val TYPEALIAS_KEYWORD: IElementType = KtTokens.TYPE_ALIAS_KEYWORD
  public val CLASS_KEYWORD: IElementType = KtTokens.CLASS_KEYWORD
  public val THIS_KEYWORD: IElementType = KtTokens.THIS_KEYWORD
  public val SUPER_KEYWORD: IElementType = KtTokens.SUPER_KEYWORD
  public val VAL_KEYWORD: IElementType = KtTokens.VAL_KEYWORD
  public val VAR_KEYWORD: IElementType = KtTokens.VAR_KEYWORD
  public val FUN_KEYWORD: IElementType = KtTokens.FUN_KEYWORD
  public val FOR_KEYWORD: IElementType = KtTokens.FOR_KEYWORD
  public val NULL_KEYWORD: IElementType = KtTokens.NULL_KEYWORD
  public val TRUE_KEYWORD: IElementType = KtTokens.TRUE_KEYWORD
  public val FALSE_KEYWORD: IElementType = KtTokens.FALSE_KEYWORD
  public val IS_KEYWORD: IElementType = KtTokens.IS_KEYWORD
  public val IN_KEYWORD: IElementType = KtTokens.IN_KEYWORD
  public val THROW_KEYWORD: IElementType = KtTokens.THROW_KEYWORD
  public val RETURN_KEYWORD: IElementType = KtTokens.RETURN_KEYWORD
  public val BREAK_KEYWORD: IElementType = KtTokens.BREAK_KEYWORD
  public val CONTINUE_KEYWORD: IElementType = KtTokens.CONTINUE_KEYWORD
  public val OBJECT_KEYWORD: IElementType = KtTokens.OBJECT_KEYWORD
  public val IF_KEYWORD: IElementType = KtTokens.IF_KEYWORD
  public val TRY_KEYWORD: IElementType = KtTokens.TRY_KEYWORD
  public val ELSE_KEYWORD: IElementType = KtTokens.ELSE_KEYWORD
  public val WHILE_KEYWORD: IElementType = KtTokens.WHILE_KEYWORD
  public val DO_KEYWORD: IElementType = KtTokens.DO_KEYWORD
  public val WHEN_KEYWORD: IElementType = KtTokens.WHEN_KEYWORD
  public val INTERFACE_KEYWORD: IElementType = KtTokens.INTERFACE_KEYWORD
  public val TYPEOF_KEYWORD: IElementType = KtTokens.TYPEOF_KEYWORD
  public val AS_SAFE: IElementType = KtTokens.AS_SAFE
  public val IDENTIFIER: IElementType = KtTokens.IDENTIFIER
  public val FIELD_IDENTIFIER: IElementType = KtTokens.FIELD_IDENTIFIER
  public val LBRACKET: IElementType = KtTokens.LBRACKET
  public val RBRACKET: IElementType = KtTokens.RBRACKET
  public val LBRACE: IElementType = KtTokens.LBRACE
  public val RBRACE: IElementType = KtTokens.RBRACE
  public val LPAR: IElementType = KtTokens.LPAR
  public val RPAR: IElementType = KtTokens.RPAR
  public val DOT: IElementType = KtTokens.DOT
  public val PLUSPLUS: IElementType = KtTokens.PLUSPLUS
  public val MINUSMINUS: IElementType = KtTokens.MINUSMINUS
  public val MUL: IElementType = KtTokens.MUL
  public val PLUS: IElementType = KtTokens.PLUS
  public val MINUS: IElementType = KtTokens.MINUS
  public val EXCL: IElementType = KtTokens.EXCL
  public val DIV: IElementType = KtTokens.DIV
  public val PERC: IElementType = KtTokens.PERC
  public val LT: IElementType = KtTokens.LT
  public val GT: IElementType = KtTokens.GT
  public val LTEQ: IElementType = KtTokens.LTEQ
  public val GTEQ: IElementType = KtTokens.GTEQ
  public val EQEQEQ: IElementType = KtTokens.EQEQEQ
  public val ARROW: IElementType = KtTokens.ARROW
  public val DOUBLE_ARROW: IElementType = KtTokens.DOUBLE_ARROW
  public val EXCLEQEQEQ: IElementType = KtTokens.EXCLEQEQEQ
  public val EQEQ: IElementType = KtTokens.EQEQ
  public val EXCLEQ: IElementType = KtTokens.EXCLEQ
  public val EXCLEXCL: IElementType = KtTokens.EXCLEXCL
  public val ANDAND: IElementType = KtTokens.ANDAND
  public val OROR: IElementType = KtTokens.OROR
  public val SAFE_ACCESS: IElementType = KtTokens.SAFE_ACCESS
  public val ELVIS: IElementType = KtTokens.ELVIS
  public val QUEST: IElementType = KtTokens.QUEST
  public val COLONCOLON: IElementType = KtTokens.COLONCOLON
  public val COLON: IElementType = KtTokens.COLON
  public val SEMICOLON: IElementType = KtTokens.SEMICOLON
  public val DOUBLE_SEMICOLON: IElementType = KtTokens.DOUBLE_SEMICOLON
  public val RANGE: IElementType = KtTokens.RANGE
  public val RANGE_UNTIL: IElementType = KtTokens.RANGE_UNTIL
  public val EQ: IElementType = KtTokens.EQ
  public val MULTEQ: IElementType = KtTokens.MULTEQ
  public val DIVEQ: IElementType = KtTokens.DIVEQ
  public val PERCEQ: IElementType = KtTokens.PERCEQ
  public val PLUSEQ: IElementType = KtTokens.PLUSEQ
  public val MINUSEQ: IElementType = KtTokens.MINUSEQ
  public val NOT_IN: IElementType = KtTokens.NOT_IN
  public val NOT_IS: IElementType = KtTokens.NOT_IS
  public val HASH: IElementType = KtTokens.HASH
  public val AT: IElementType = KtTokens.AT
  public val COMMA: IElementType = KtTokens.COMMA
  public val EOL_OR_SEMICOLON: IElementType = KtTokens.EOL_OR_SEMICOLON
  public val FILE_KEYWORD: IElementType = KtTokens.FILE_KEYWORD
  public val FIELD_KEYWORD: IElementType = KtTokens.FIELD_KEYWORD
  public val PROPERTY_KEYWORD: IElementType = KtTokens.PROPERTY_KEYWORD
  public val RECEIVER_KEYWORD: IElementType = KtTokens.RECEIVER_KEYWORD
  public val PARAM_KEYWORD: IElementType = KtTokens.PARAM_KEYWORD
  public val SETPARAM_KEYWORD: IElementType = KtTokens.SETPARAM_KEYWORD
  public val DELEGATE_KEYWORD: IElementType = KtTokens.DELEGATE_KEYWORD
  public val IMPORT_KEYWORD: IElementType = KtTokens.IMPORT_KEYWORD
  public val WHERE_KEYWORD: IElementType = KtTokens.WHERE_KEYWORD
  public val BY_KEYWORD: IElementType = KtTokens.BY_KEYWORD
  public val GET_KEYWORD: IElementType = KtTokens.GET_KEYWORD
  public val SET_KEYWORD: IElementType = KtTokens.SET_KEYWORD
  public val CONSTRUCTOR_KEYWORD: IElementType = KtTokens.CONSTRUCTOR_KEYWORD
  public val INIT_KEYWORD: IElementType = KtTokens.INIT_KEYWORD
  public val ABSTRACT_KEYWORD: IElementType = KtTokens.ABSTRACT_KEYWORD
  public val ENUM_KEYWORD: IElementType = KtTokens.ENUM_KEYWORD
  public val OPEN_KEYWORD: IElementType = KtTokens.OPEN_KEYWORD
  public val INNER_KEYWORD: IElementType = KtTokens.INNER_KEYWORD
  public val OVERRIDE_KEYWORD: IElementType = KtTokens.OVERRIDE_KEYWORD
  public val PRIVATE_KEYWORD: IElementType = KtTokens.PRIVATE_KEYWORD
  public val PUBLIC_KEYWORD: IElementType = KtTokens.PUBLIC_KEYWORD
  public val INTERNAL_KEYWORD: IElementType = KtTokens.INTERNAL_KEYWORD
  public val PROTECTED_KEYWORD: IElementType = KtTokens.PROTECTED_KEYWORD
  public val CATCH_KEYWORD: IElementType = KtTokens.CATCH_KEYWORD
  public val OUT_KEYWORD: IElementType = KtTokens.OUT_KEYWORD
  public val VARARG_KEYWORD: IElementType = KtTokens.VARARG_KEYWORD
  public val REIFIED_KEYWORD: IElementType = KtTokens.REIFIED_KEYWORD
  public val DYNAMIC_KEYWORD: IElementType = KtTokens.DYNAMIC_KEYWORD
  public val COMPANION_KEYWORD: IElementType = KtTokens.COMPANION_KEYWORD
  public val SEALED_KEYWORD: IElementType = KtTokens.SEALED_KEYWORD
  public val DEFAULT_VISIBILITY_KEYWORD: IElementType = PUBLIC_KEYWORD
  public val FINALLY_KEYWORD: IElementType = KtTokens.FINALLY_KEYWORD
  public val FINAL_KEYWORD: IElementType = KtTokens.FINAL_KEYWORD
  public val LATEINIT_KEYWORD: IElementType = KtTokens.LATEINIT_KEYWORD
  public val DATA_KEYWORD: IElementType = KtTokens.DATA_KEYWORD
  public val INLINE_KEYWORD: IElementType = KtTokens.INLINE_KEYWORD
  public val NOINLINE_KEYWORD: IElementType = KtTokens.NOINLINE_KEYWORD
  public val TAILREC_KEYWORD: IElementType = KtTokens.TAILREC_KEYWORD
  public val EXTERNAL_KEYWORD: IElementType = KtTokens.EXTERNAL_KEYWORD
  public val ANNOTATION_KEYWORD: IElementType = KtTokens.ANNOTATION_KEYWORD
  public val CROSSINLINE_KEYWORD: IElementType = KtTokens.CROSSINLINE_KEYWORD
  public val OPERATOR_KEYWORD: IElementType = KtTokens.OPERATOR_KEYWORD
  public val INFIX_KEYWORD: IElementType = KtTokens.INFIX_KEYWORD
  public val CONST_KEYWORD: IElementType = KtTokens.CONST_KEYWORD
  public val SUSPEND_KEYWORD: IElementType = KtTokens.SUSPEND_KEYWORD

  // public val HEADER_KEYWORD: IElementType = KtTokens.HEADER_KEYWORD
  // public val IMPL_KEYWORD: IElementType = KtTokens.IMPL_KEYWORD
  public val EXPECT_KEYWORD: IElementType = KtTokens.EXPECT_KEYWORD
  public val ACTUAL_KEYWORD: IElementType = KtTokens.ACTUAL_KEYWORD

  // KDocTokens
  public val KDOC: IElementType = KDocTokens.KDOC
  public val KDOC_START: IElementType = KDocTokens.START
  public val KDOC_END: IElementType = KDocTokens.END
  public val KDOC_LEADING_ASTERISK: IElementType = KDocTokens.LEADING_ASTERISK
  public val KDOC_TEXT: IElementType = KDocTokens.TEXT
  public val KDOC_CODE_BLOCK_TEXT: IElementType = KDocTokens.CODE_BLOCK_TEXT
  public val KDOC_TAG_NAME: IElementType = KDocTokens.TAG_NAME
  public val KDOC_MARKDOWN_LINK: IElementType = KDocTokens.MARKDOWN_LINK
  public val KDOC_MARKDOWN_ESCAPED_CHAR: IElementType = KDocTokens.MARKDOWN_ESCAPED_CHAR
  public val KDOC_MARKDOWN_INLINE_LINK: IElementType = KDocTokens.MARKDOWN_INLINE_LINK
  public val KDOC_SECTION: IElementType = KDocElementTypes.KDOC_SECTION
  public val KDOC_TAG: IElementType = KDocElementTypes.KDOC_TAG
  public val KDOC_NAME: IElementType = KDocElementTypes.KDOC_NAME
}

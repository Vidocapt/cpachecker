/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.sosy_lab.cpachecker.util;

import com.google.common.collect.ImmutableSet;

/** List of functions defined by standards */
public final class StandardFunctions {

  /** C11 B.1 Diagnostics assert.h * */
  public static final ImmutableSet<String> C11_ASSERT_H_FUNCTIONS = ImmutableSet.of("assert");

  /** C11 B.5 Floating-point environment fenv.h * */
  public static final ImmutableSet<String> C11_FENV_H_FUNCTIONS =
      ImmutableSet.of(
          "feclearexcept",
          "fegetexceptflag",
          "feraiseexcept",
          "fesetexceptflag",
          "fetestexcept",
          "fegetround",
          "fesetround",
          "fegetenv",
          "feholdexcept",
          "fesetenv",
          "feupdateenv");

  /** C11 B.11 Mathematics math.h */
  public static final ImmutableSet<String> C11_MATH_H_FUNCTIONS =
      ImmutableSet.of(
          "acos",
          "acosf",
          "acosh",
          "acoshf",
          "acoshl",
          "acosl",
          "asin",
          "asinf",
          "asinh",
          "asinhf",
          "asinhl",
          "asinl",
          "atan",
          "atan2",
          "atan2f",
          "atan2l",
          "atanf",
          "atanh",
          "atanhf",
          "atanhl",
          "atanl",
          "cbrt",
          "cbrtf",
          "cbrtl",
          "ceil",
          "ceilf",
          "ceill",
          "copysign",
          "copysignf",
          "copysignl",
          "cos",
          "cosf",
          "cosh",
          "coshf",
          "coshl",
          "cosl",
          "erf",
          "erfc",
          "erfcf",
          "erfcl",
          "erff",
          "erfl",
          "exp",
          "exp2",
          "exp2f",
          "exp2l",
          "expf",
          "expl",
          "expm1",
          "expm1f",
          "expm1l",
          "fabs",
          "fabsf",
          "fabsl",
          "fdim",
          "fdimf",
          "fdiml",
          "floor",
          "floorf",
          "floorl",
          "fma",
          "fmaf",
          "fmal",
          "fmax",
          "fmaxf",
          "fmaxl",
          "fmin",
          "fminf",
          "fminl",
          "fmod",
          "fmodf",
          "fmodl",
          "fpclassify",
          "frexp",
          "frexpf",
          "frexpl",
          "hypot",
          "hypotf",
          "hypotl",
          "ilogb",
          "ilogbf",
          "ilogbl",
          "isfinite",
          "isgreater",
          "isgreaterequal",
          "isinf",
          "isless",
          "islessequal",
          "islessgreater",
          "isnan",
          "isnormal",
          "isunordered",
          "ldexp",
          "ldexpf",
          "ldexpl",
          "lgamma",
          "lgammaf",
          "lgammal",
          "llrint",
          "llrintf",
          "llrintl",
          "llround",
          "llroundf",
          "llroundl",
          "log",
          "log10",
          "log10f",
          "log10l",
          "log1p",
          "log1pf",
          "log1pl",
          "log2",
          "log2f",
          "log2l",
          "logb",
          "logbf",
          "logbl",
          "logf",
          "logl",
          "lrint",
          "lrintf",
          "lrintl",
          "lround",
          "lroundf",
          "lroundl",
          "modf",
          "modff",
          "modfl",
          "nan",
          "nanf",
          "nanl",
          "nearbyint",
          "nearbyintf",
          "nearbyintl",
          "nextafter",
          "nextafterf",
          "nextafterl",
          "nexttoward",
          "nexttowardf",
          "nexttowardl",
          "pow",
          "powf",
          "powl",
          "remainder",
          "remainderf",
          "remainderl",
          "remquo",
          "remquof",
          "remquol",
          "rint",
          "rintf",
          "rintl",
          "round",
          "roundf",
          "roundl",
          "scalbln",
          "scalblnf",
          "scalblnl",
          "scalbn",
          "scalbnf",
          "scalbnl",
          "signbit",
          "sin",
          "sinf",
          "sinh",
          "sinhf",
          "sinhl",
          "sinl",
          "sqrt",
          "sqrtf",
          "sqrtl",
          "tan",
          "tanf",
          "tanh",
          "tanhf",
          "tanhl",
          "tanl",
          "tgamma",
          "tgammaf",
          "tgammal",
          "trunc",
          "truncf",
          "truncl");

  /** C11 B.12 Nonlocal jumps stdio.h * */
  public static final ImmutableSet<String> C11_SETJUMP_H_FUNCTIONS =
      ImmutableSet.of("setjmp", "longjmp");

  /** C11 B.20 Input/output stdio.h * */
  public static final ImmutableSet<String> C11_STDIO_H_FUNCTIONS =
      ImmutableSet.of(
          "clearerr",
          "fclose",
          "feof",
          "ferror",
          "fflush",
          "fgetc",
          "fgetpos",
          "fgets",
          "fopen",
          "fopen_s",
          "fprintf",
          "fprintf_s",
          "fputc",
          "fputs",
          "fread",
          "freopen",
          "freopen_s",
          "fscanf",
          "fscanf_s",
          "fseek",
          "fsetpos",
          "ftell",
          "fwrite",
          "getc",
          "getchar",
          "gets_s",
          "perror",
          "printf",
          "printf_s",
          "putc",
          "putchar",
          "puts",
          "remove",
          "rename",
          "rewind",
          "scanf",
          "scanf_s",
          "setbuf",
          "setvbuf",
          "snprintf",
          "snprintf_s",
          "sprintf",
          "sprintf_s",
          "sscanf",
          "sscanf_s",
          "tmpfile",
          "tmpfile_s",
          "tmpnam",
          "tmpnam_s",
          "ungetc",
          "vfprintf",
          "vfprintf_s",
          "vfscanf",
          "vfscanf_s",
          "vprintf",
          "vprintf_s",
          "vscanf",
          "vscanf_s",
          "vsnprintf",
          "vsnprintf_s",
          "vsprintf",
          "vsprintf_s",
          "vsscanf",
          "vsscanf_s");

  /** C11 B.21 Input/output stdlib.h * */
  public static final ImmutableSet<String> C11_STDLIB_H_FUNCTIONS =
      ImmutableSet.of(
          "_Exit",
          "abort",
          "abort_handler_s",
          "abs",
          "aligned_alloc",
          "at_quick_exit",
          "atexit",
          "atof",
          "atoi",
          "atol",
          "atoll",
          "bsearch",
          "bsearch_s",
          "calloc",
          "div",
          "exit",
          "free",
          "getenv",
          "getenv_s",
          "ignore_handler_s",
          "labs",
          "ldiv",
          "llabs",
          "lldiv",
          "malloc",
          "mblen",
          "mbstowcs",
          "mbstowcs_s",
          "mbtowc",
          "qsort",
          "qsort_s",
          "quick_exit",
          "rand",
          "realloc",
          "set_constraint_handler_s",
          "srand",
          "strtod",
          "strtof",
          "strtol",
          "strtold",
          "strtoll",
          "strtoul",
          "strtoull",
          "system",
          "wcstombs",
          "wcstombs_s",
          "wctomb",
          "wctomb_s");

  /** C11 B.23 String handling string.h * */
  public static final ImmutableSet<String> C11_STRING_H_FUNCTIONS =
      ImmutableSet.of(
          "memchr",
          "memcmp",
          "memcpy",
          "memcpy_s",
          "memmove",
          "memmove_s",
          "memset",
          "memset_s",
          "strcat",
          "strcat_s",
          "strchr",
          "strcmp",
          "strcoll",
          "strcpy",
          "strcpy_s",
          "strcspn",
          "strerror",
          "strerror_s",
          "strerrorlen_s",
          "strlen",
          "strncat",
          "strncat_s",
          "strncmp",
          "strncpy",
          "strncpy_s",
          "strnlen_s",
          "strpbrk",
          "strrchr",
          "strspn",
          "strstr",
          "strtok",
          "strtok_s",
          "strxfrm");

  /** C11 B.26 Date and time time.h * */
  public static final ImmutableSet<String> C11_TIME_H_FUNCTIONS =
      ImmutableSet.of(
          "clock",
          "difftime",
          "mktime",
          "time",
          "timespec_get",
          "asctime",
          "ctime",
          "gmtime",
          "localtime",
          "strftime",
          "asctime_s",
          "ctime_s",
          "gmtime_s",
          "localtime_s");

  /** C11 B.28 Extended multibyte/wide character utilities wchar.h */
  public static final ImmutableSet<String> C11_WCHAR_H_FUNCTIONS =
      ImmutableSet.of(
          "fwprintf",
          "fwscanf",
          "swprintf",
          "swscanf",
          "vfwprintf",
          "vfwscanf",
          "vswprintf",
          "vswscanf",
          "vwprintf",
          "vwscanf",
          "wprintf",
          "wscanf",
          "fgetwc",
          "fgetws",
          "fputwc",
          "fputws",
          "fwide",
          "getwc",
          "getwchar",
          "putwc",
          "putwchar",
          "ungetwc",
          "wcstod",
          "wcstof",
          "wcstold",
          "wcstol",
          "wcstoll",
          "wcstoul",
          "wcstoull",
          "wcscpy",
          "wcsncpy",
          "wmemcpy",
          "wmemmove",
          "wcscat",
          "wcsncat",
          "wcscmp",
          "wcscoll",
          "wcsncmp",
          "wcsxfrm",
          "wmemcmp",
          "wcschr",
          "wcscspn",
          "wcspbrk",
          "wcsrchr",
          "wcsspn",
          "wcsstr",
          "wcstok",
          "wmemchr",
          "wcslen",
          "wmemset",
          "wcsftime",
          "btowc",
          "wctob",
          "mbsinit",
          "mbrlen",
          "mbrtowc",
          "wcrtomb",
          "mbsrtowcs",
          "wcsrtombs",
          "fwprintf_s",
          "fwscanf_s",
          "snwprintf_s",
          "swprintf_s",
          "swscanf_s",
          "vfwprintf_s",
          "vfwscanf_s",
          "vsnwprintf_s",
          "vswprintf_s",
          "vswscanf_s",
          "vwprintf_s",
          "vwscanf_s",
          "wprintf_s",
          "wscanf_s",
          "wcscpy_s",
          "wcsncpy_s",
          "wmemcpy_s",
          "wmemmove_s",
          "wcscat_s",
          "wcsncat_s",
          "wcstok_s",
          "wcsnlen_s",
          "wcrtomb_s",
          "mbsrtowcs_s",
          "wcsrtombs_s");

  /** All functions defined by C11 from the other sets in this file */
  public static final ImmutableSet<String> C11_ALL_FUNCTIONS =
      ImmutableSet.<String>builder()
          .addAll(C11_ASSERT_H_FUNCTIONS)
          .addAll(C11_FENV_H_FUNCTIONS)
          .addAll(C11_MATH_H_FUNCTIONS)
          .addAll(C11_SETJUMP_H_FUNCTIONS)
          .addAll(C11_STDIO_H_FUNCTIONS)
          .addAll(C11_STDLIB_H_FUNCTIONS)
          .addAll(C11_STRING_H_FUNCTIONS)
          .addAll(C11_TIME_H_FUNCTIONS)
          .addAll(C11_WCHAR_H_FUNCTIONS)
          .build();

  /** GNU C alloca.h */
  public static final ImmutableSet<String> GNUC_ALLOCA_H_FUNCTIONS = ImmutableSet.of("alloca");

  /**
   * All functions defined by GNU C from the other sets in this file (not including the C11 and
   * POSIX functions).
   */
  public static final ImmutableSet<String> GNUC_ALL_FUNCTIONS = GNUC_ALLOCA_H_FUNCTIONS;

  /** POSIX unistd.h from http://pubs.opengroup.org/onlinepubs/9699919799/basedefs/unistd.h.html */
  public static final ImmutableSet<String> POSIX_UNISTD_H_FUNCTIONS =
      ImmutableSet.of(
          "access",
          "alarm",
          "chdir",
          "chown",
          "close",
          "confstr",
          "crypt",
          "dup",
          "dup2",
          "_exit",
          "encrypt",
          "execl",
          "execle",
          "execlp",
          "execv",
          "execve",
          "execvp",
          "faccessat",
          "fchdir",
          "fchown",
          "fchownat",
          "fdatasync",
          "fexecve",
          "fork",
          "fpathconf",
          "fsync",
          "ftruncate",
          "getcwd",
          "getegid",
          "geteuid",
          "getgid",
          "getgroups",
          "gethostid",
          "gethostname",
          "getlogin",
          "getlogin_r",
          "getopt",
          "getpgid",
          "getpgrp",
          "getpid",
          "getppid",
          "getsid",
          "getuid",
          "isatty",
          "lchown",
          "link",
          "linkat",
          "lockf",
          "lseek",
          "nice",
          "pathconf",
          "pause",
          "pipe",
          "pread",
          "pwrite",
          "read",
          "readlink",
          "readlinkat",
          "rmdir",
          "setegid",
          "seteuid",
          "setgid",
          "setpgid",
          "setpgrp",
          "setregid",
          "setreuid",
          "setsid",
          "setuid",
          "sleep",
          "swab",
          "symlink",
          "symlinkat",
          "sync",
          "sysconf",
          "tcgetpgrp",
          "tcsetpgrp",
          "truncate",
          "ttyname",
          "ttyname_r",
          "unlink",
          "unlinkat",
          "write");

  /** All functions defined by POSIX from the other sets in this file */
  public static final ImmutableSet<String> POSIX_ALL_FUNCTIONS = POSIX_UNISTD_H_FUNCTIONS;
}

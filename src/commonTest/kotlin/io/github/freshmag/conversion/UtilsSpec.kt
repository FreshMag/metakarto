package io.github.freshmag.conversion

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UtilsSpec : StringSpec({
    "variationsOf single name returns all variations" {
        val result = variationsOf("my_Name")
        val expected = setOf("my_Name", "my_name", "MY_NAME", "My_Name", "myName", "MYNAME", "MyName", "myname")
        result shouldBe expected
    }

    "variationsOf multiple names returns all variations" {
        val result = variationsOf("my_Name", "anotherName")
        val expected =
            setOf(
                "my_Name",
                "my_name",
                "MY_NAME",
                "My_Name",
                "myName",
                "MYNAME",
                "MyName",
                "myname",
                "anotherName",
                "anothername",
                "ANOTHERNAME",
                "AnotherName",
                "anothername",
            )
        result shouldBe expected
    }

    "variationsOf empty string returns empty set" {
        val result = variationsOf("")
        val expected = emptySet<String>()
        result shouldBe expected
    }

    "variationsOf special characters returns variations without special characters" {
        val result = variationsOf("my@Name!")
        val expected = setOf("my@Name!", "my@name!", "MY@NAME!", "My@Name!", "myName", "MYNAME", "MyName", "myname")
        result shouldBe expected
    }

    "variationsOf numeric characters returns variations with numeric characters" {
        val result = variationsOf("myName123")
        val expected = setOf("myName123", "myname123", "MYNAME123", "MyName123", "myname123")
        result shouldBe expected
    }
})

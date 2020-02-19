package net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.sequence

import crafttweaker.annotations.ZenRegister
import net.thesilkminer.mc.boson.compatibility.crafttweaker.toZen
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.BiFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.BiPredicate
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Consumer
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Function
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.IntFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ObjIntConsumer
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.Predicate
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToDoubleFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToIntBiFunction
import net.thesilkminer.mc.boson.compatibility.crafttweaker.zenscriptx.function.ToIntFunction
import stanhebben.zenscript.annotations.ZenClass

@ZenClass("zenscriptx.sequence.Sequence")
@ZenRegister
class ZenSequence<T>(val sequence: Sequence<T>) {
    constructor(contents: Array<T>) : this(contents.asSequence())
    constructor(contents: List<T>) : this(contents.asSequence())

    fun contains(element: T) = this.sequence.contains(element)
    fun elementAt(index: Int) = this.sequence.elementAt(index)
    fun elementAtOrElse(index: Int, defaultValue: IntFunction<T>) = this.sequence.elementAtOrElse(index, defaultValue.toFunction())
    fun elementAtOrNull(index: Int) = this.sequence.elementAtOrNull(index)
    fun find(predicate: Predicate<T>) = this.sequence.find(predicate.toFunction())
    fun findLast(predicate: Predicate<T>) = this.sequence.findLast(predicate.toFunction())
    fun first() = this.sequence.first()
    fun first(predicate: Predicate<T>) = this.sequence.first(predicate.toFunction())
    fun firstOrNull() = this.sequence.firstOrNull()
    fun firstOrNull(predicate: Predicate<T>) = this.sequence.firstOrNull(predicate.toFunction())
    fun indexOf(element: T) = this.sequence.indexOf(element)
    fun indexOfFirst(predicate: Predicate<T>) = this.sequence.indexOfFirst(predicate.toFunction())
    fun indexOfLast(predicate: Predicate<T>) = this.sequence.indexOfLast(predicate.toFunction())
    fun last() = this.sequence.last()
    fun last(predicate: Predicate<T>) = this.sequence.last(predicate.toFunction())
    fun lastIndexOf(element: T) = this.sequence.lastIndexOf(element)
    fun lastOrNull() = this.sequence.lastOrNull()
    fun lastOrNull(predicate: Predicate<T>) = this.sequence.lastOrNull(predicate.toFunction())
    fun single() = this.sequence.single()
    fun single(predicate: Predicate<T>) = this.sequence.single(predicate.toFunction())
    fun singleOrNull() = this.sequence.singleOrNull()
    fun singleOrNull(predicate: Predicate<T>) = this.sequence.singleOrNull(predicate.toFunction())
    fun drop(n: Int) = this.sequence.drop(n).toZen()
    fun dropWhile(predicate: Predicate<T>) = this.sequence.dropWhile(predicate.toFunction()).toZen()
    fun filter(predicate: Predicate<T>) = this.sequence.filter(predicate.toFunction()).toZen()
    fun filterIndexed(predicate: BiPredicate<Int, T>) = this.sequence.filterIndexed(predicate.toFunction()).toZen()
    // filterIndexedTo
    // filterIsInstance
    // filterIsInstanceTo
    fun filterNot(predicate: Predicate<T>) = this.sequence.filterNot(predicate.toFunction()).toZen()
    // filterNotNull (unable to with Kotlin)
    // filterNotNullTo
    // filterNotTo
    // filterTo
    fun take(n: Int) = this.sequence.take(n).toZen()
    fun takeWhile(predicate: Predicate<T>) = this.sequence.takeWhile(predicate.toFunction()).toZen()
    // sorted
    // sortedBy
    // sortedByDescending
    // sortedDescending
    fun sortedWith(comparator: ToIntBiFunction<T, T>) = this.sequence.sortedWith(comparator.toComparator()).toZen()
    // associate
    // associateBy
    // associateBy
    // associateByTo
    // associateByTo
    // associateTo
    // associateWith
    // associateWithTo
    // toCollection
    // toHashSet
    fun toList() = this.sequence.toList()
    // toMutableList
    // toSet
    // flatMap (ZenSequence and Sequence aren't that happy together)
    // flatMapTo
    // groupBy
    // groupBy
    // groupByTo
    // groupByTo
    // groupingBy
    fun <R> map(transform: Function<T, R>) = this.sequence.map(transform.toFunction()).toZen()
    fun <R> mapIndexed(transform: BiFunction<Int, T, R>) = this.sequence.mapIndexed(transform.toFunction()).toZen()
    // mapIndexedNotNull
    // mapIndexedNotNullTo
    // mapIndexedTo
    // mapNotNull
    // mapNotNullTo
    // mapTo
    // withIndex
    fun distinct() = this.sequence.distinct().toZen()
    fun <K> distinctBy(selector: Function<T, K>) = this.sequence.distinctBy(selector.toFunction()).toZen()
    // toMutableSet
    fun all(predicate: Predicate<T>) = this.sequence.all(predicate.toFunction())
    fun any() = this.sequence.any()
    fun any(predicate: Predicate<T>) = this.sequence.any(predicate.toFunction())
    fun count() = this.sequence.count()
    fun count(predicate: Predicate<T>) = this.sequence.count(predicate.toFunction())
    fun <R> fold(initial: R, operation: BiFunction<R, T, R>) = this.sequence.fold(initial, operation.toFunction())
    // foldIndexed
    fun forEach(action: Consumer<T>) = this.sequence.forEach(action.toFunction())
    fun forEachIndexed(action: ObjIntConsumer<T>) = this.sequence.forEachIndexed(action.toFunction())
    // max
    // max
    // max
    // maxBy
    fun maxWith(comparator: ToIntBiFunction<T, T>) = this.sequence.maxWith(comparator.toComparator())
    // min
    // min
    // min
    // minBy
    fun minWith(comparator: ToIntBiFunction<T, T>) = this.sequence.minWith(comparator.toComparator())
    fun none() = this.sequence.none()
    fun none(predicate: Predicate<T>) = this.sequence.none(predicate.toFunction())
    fun onEach(action: Consumer<T>) = this.sequence.onEach(action.toFunction()).toZen()
    // reduce
    // reduceIndexed
    fun sumBy(selector: ToIntFunction<T>) = this.sequence.sumBy(selector.toFunction())
    fun sumByDouble(selector: ToDoubleFunction<T>) = this.sequence.sumByDouble(selector.toFunction())
    // requireNoNulls
    // chunked
    // chunked
    fun minus(element: T) = this.sequence.minus(element).toZen()
    fun minus(elements: Array<T>) = this.sequence.minus(elements).toZen()
    // minus
    // minus
    fun minusElement(element: T) = this.sequence.minusElement(element).toZen()
    // partition
    fun plus(element: T) = this.sequence.plus(element).toZen()
    fun plus(elements: Array<T>) = this.sequence.plus(elements).toZen()
    // plus
    // plus
    fun plusElement(element: T) = this.sequence.plusElement(element).toZen()
    // windowed
    // windowed
    // zip
    // zip
    // zipWithNext
    fun <R> zipWithNext(transform: BiFunction<T, T, R>) = this.sequence.zipWithNext(transform.toFunction()).toZen()
    // joinTo
    fun joinToString(separator: String?, prefix: String?, postfix: String?, limit: Int, truncated: String?, transform: Function<T, String>?) =
            this.sequence.joinToString(separator ?: ", ", prefix ?: "", postfix ?: "", limit, truncated ?: "...", transform?.toFunction())
    // asIterable
    fun asSequence() = this.sequence.asSequence().toZen()
    // average
    // average
    // average
    // average
    // average
    // average
    // sum
    // sum
    // sum
    // sum
    // sum
    // sum

    private fun <T> IntFunction<T>.toFunction() = { it: Int -> this.apply(it) }
    private fun <T> Predicate<T>.toFunction() = { it: T -> this.test(it) }
    private fun <T, R> BiPredicate<T, R>.toFunction() = { a: T, b: R -> this.test(a, b) }
    private fun <T, R> Function<T, R>.toFunction() = { it: T -> this.apply(it) }
    private fun <T, U, R> BiFunction<T, U, R>.toFunction() = { a: T, b: U -> this.apply(a, b) }
    private fun <T> Consumer<T>.toFunction() = { it: T -> this.accept(it) }
    private fun <T> ObjIntConsumer<T>.toFunction() = { index: Int, it: T -> this.accept(it, index) }
    private fun <T> ToIntFunction<T>.toFunction() = { it: T -> this.apply(it) }
    private fun <T> ToDoubleFunction<T>.toFunction() = { it: T -> this.apply(it) }

    private fun <T> ToIntBiFunction<T, T>.toComparator() = Comparator { a: T, b: T -> this.apply(a, b) }
}

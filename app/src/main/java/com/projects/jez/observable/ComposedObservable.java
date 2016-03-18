package com.projects.jez.observable;

public class ComposedObservable<T> extends Observable<T> {

    private ComposedObservable(Generator<T> generator) {
        super(generator);
    }

    static <T> ComposedObservable<T> merge(final Observable<T> observable1, final Observable<T> observable2) {
        return new ComposedObservable<T>(new Generator<T>() {
            @Override
            public void generate(Putter<T> putter) {
                observable1.addObserverImmediate(putter);
                observable2.addObserverImmediate(putter);
            }
        });
    }

    static <A, B, C> ComposedObservable<C> combine(
            final Observable<A> observable1,
            final Observable<B> observable2,
            final Combiner<A, B, C> combiner) {
        return new ComposedObservable<C>(new Generator<C>() {
            @Override
            public void generate(final Putter<C> putter) {
                // initial value
                final NullSafeCombiner<A, B, C> safeCombiner = new NullSafeCombiner<A, B, C>(combiner);
                C c = safeCombiner.combine(observable1.getCurrent(), observable2.getCurrent());
                putter.put(c);

                // update value when observers change
                observable1.addObserverImmediate(new Observer<A>() {
                    @Override
                    public void observe(A arg) {
                        C c = safeCombiner.combine(arg, observable2.getCurrent());
                        putter.put(c);
                    }
                });
                observable2.addObserverImmediate(new Observer<B>() {
                    @Override
                    public void observe(B arg) {
                        C c = safeCombiner.combine(observable1.getCurrent(), arg);
                        putter.put(c);
                    }
                });
            }
        });
    }

    public interface Combiner <A, B, C> {
        public C combine(A a, B b);
    }

    private static class NullSafeCombiner<A, B, C> implements Combiner<A, B, C> {
        Combiner<A, B, C> mCombiner;

        private NullSafeCombiner(Combiner<A, B, C> combiner) {
            mCombiner = combiner;
        }

        @Override
        public C combine(A a, B b) {
            if (a == null || b == null) {
                return null;
            }
            return mCombiner.combine(a, b);
        }
    }
}

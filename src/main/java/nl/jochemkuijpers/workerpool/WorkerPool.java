package nl.jochemkuijpers.workerpool;

import java.util.Deque;
import java.util.LinkedList;

/**
 * The worker pool starts a number of worker threads. Each worker waits for jobs to become available and executes them.
 * Much like Javas own ExecutorService, though this one can interrupt jobs without terminating, which is useful for
 * making the ray tracer responsive.
 */
public class WorkerPool {
    private final Deque<Runnable> jobQueue;
    private final Thread[] threads;

    private boolean stop = false;

    public WorkerPool(int numThreads) {
        jobQueue = new LinkedList<>();

        threads = new Thread[numThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Worker(), "workerpool-" + i);
            threads[i].start();
        }
    }

    /**
     * This method may return false before the last jobs are completely executed.
     * @return whether there is still work left in the queue
     */
    public boolean hasWork() {
        synchronized (jobQueue) {
            return jobQueue.size() > 0;
        }
    }

    /**
     * Clears all jobs currently in the queue
     */
    public void clearPendingJobs() {
        synchronized (jobQueue) {
            jobQueue.clear();
        }
    }

    /**
     * Submits a new job to the job queue. A worker will pick it up when all other jobs already in the queue have been
     * started (and possibly finished).
     *
     * @param job the queued job
     * @throws IllegalStateException if the worker pool does not accept new jobs.
     */
    public void submit(Runnable job) {
        if (stop) {
            throw new IllegalStateException("The worker pool has already been stopped. No more work is accepted.");
        }
        synchronized (jobQueue) {
            jobQueue.add(job);
            jobQueue.notifyAll();
        }
    }

    /**
     * Initiates graceful shutdown. New jobs are no longer accepted. Existing jobs will be finished but the
     * remaining jobs in the queue are discarded. No new jobs will be accepted.
     */
    public void shutdown() {
        stop = true;
    }

    /**
     * This will not directly terminate worker threads. It will wake up waiting threads so they terminate if stop is
     * set to false. Further, jobs can read the interrupt status by Thread.interrupted() to try to avoid costly
     * operations or operations that are no longer necessary now that the job is 'interrupted'.
     *
     * Calling this does not guarantee that jobs will terminate or even that the interrupted flag is respected.
     */
    public void interruptCurrentJobs() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /**
     * Await termination of all workers. Calling this on its own will not stop the worker pool. Call this after
     * initiating a shutdown.
     *
     * @throws InterruptedException if the thread was interrupted while waiting on other threads.
     */
    public void awaitTermination() throws InterruptedException {
        stop = true;

        for (Thread worker : threads) {
            worker.join();
        }
    }

    /**
     * Stops the worker pool entirely. Active jobs are interrupted, that is, requested to stop.
     * This method is blocking until all worker threads have been joined.
     *
     * Internal InterruptExceptions are ignored as the purpose of this method is to completely dismantle the worker
     * pool. After calling this method, the worker pool cannot be re-started, nor will it accept new jobs.
     */
    public void shutdownNow() {
        shutdown();
        interruptCurrentJobs();
        try {
            awaitTermination();
        } catch (InterruptedException ignored) {}
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            Runnable job;

            outer:
            while (!stop) {
                synchronized (jobQueue) {
                    while (jobQueue.size() == 0) {
                        try {
                            jobQueue.wait();
                        } catch (InterruptedException e) {
                            continue outer;
                        }
                    }
                    job = jobQueue.removeFirst();
                }
                job.run();
            }
        }
    }
}

# Java Concurrency

## Interface Future (JDK 1.5)
A Future represents the result of an asynchronous computation. This result, will eventually appear in the Future
after the processing is complete. This interface is very useful when working with asynchronous calls and concurrent
processing.

Long-running methods are good candidates for asynchronous processing and the Future interface because, we can execute
other processes while we're waiting for the task encapsulated in the Future to complete.

### Consuming Futures

#### Using isDone() and get() to Obtains Results
The Future API has two methods that help us in asynchronous processing.

- **_Future.isDone_** tells us if the executor has finished processing the tasks. If the task is complete, it will return
  true: otherwise, it returns false.
- _**Future.get()**_ this method returns the actual result from the calculation. Notice that this method blocks the
  execution until the task is complete. So, we need check first if the task is complete by calling **_isDone()_** method.

#### Canceling a Future with cancel()
Suppose we triggered a task, but for reason, we don't care about the result anymore. We can use Future.cancel(boolean)
to tell the executor to stop the operation and interrupt its underlying thread.

### Multithreading with Thread Pools
To make our program multi-threaded, we should use a thread pool provided by the factory method **_Executors.newFixedThreadPool()_**.
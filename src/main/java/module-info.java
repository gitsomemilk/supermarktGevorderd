module Supermarket {
  requires gson;
  requires java.sql;

  exports model to gson;
  opens model to gson;
}
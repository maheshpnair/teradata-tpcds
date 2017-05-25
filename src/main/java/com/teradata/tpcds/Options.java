/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teradata.tpcds;

import io.airlift.airline.Option;

import java.util.Optional;

public class Options
{
    public static final int DEFAULT_SCALE = 1;
    public static final String DEFAULT_DIRECTORY = ".";
    public static final String DEFAULT_SUFFIX = ".dat";
    public static final String DEFAULT_TABLE = null;
    public static final String DEFAULT_NULL_STRING = "";
    public static final char DEFAULT_SEPARATOR = '|';
    public static final boolean DEFAULT_DO_NOT_TERMINATE = false;
    public static final boolean DEFAULT_NO_SEXISM = false;
    public static final int DEFAULT_PARALLELISM = 1;
    public static final boolean DEFAULT_OVERWRITE = false;

    @Option(name = {"--scale", "-s"}, title = "scale", description = "Volume of data to generate in GB (Default: 1)")
    public float scale = DEFAULT_SCALE;

    @Option(name = {"--directory", "-d"}, title = "directory", description = "Directory to put generated files (Default: .) ")
    public String directory = DEFAULT_DIRECTORY;

    @Option(name = "--suffix", title = "suffix", description = "Suffix for generated data files (Default: .dat)")
    public String suffix = DEFAULT_SUFFIX;

    @Option(name = {"--table", "-t"}, title = "table", description = "Build only the specified table.  If this property is not specified, all tables will be generated")
    public String table = DEFAULT_TABLE;

    @Option(name = {"--null"}, title = "null", description = "String representation for null values (Default: the empty string)")
    public String nullString = DEFAULT_NULL_STRING;

    @Option(name = {"--separator"}, title = "separator", description = "Separator between columns (Default: |)")
    public char separator = DEFAULT_SEPARATOR;

    @Option(name = {"--do-not-terminate"}, title = "do-not-terminate", description = "Do not terminate each row with a separator (Default: false)")
    public boolean doNotTerminate = DEFAULT_DO_NOT_TERMINATE;

    @Option(name = {"--no-sexism"}, title = "no-sexism",
            description = "The reference C implementation picks only male names for the manager fields. " +
                    "This flag instructs our generator to pick managers from a distribution of male and female names. " +
                    "*The data set generated will differ from the one generated by the official C implementation.* " +
                    "But on the other hand it won't be sexist.")
    public boolean noSexism = DEFAULT_NO_SEXISM;

    @Option(name = {"--parallelism"}, title = "parallelism", description = "Build data in <n> separate chunks (Default: 1)")
    public int parallelism = DEFAULT_PARALLELISM;

    @Option(name = {"--overwrite"}, title = "overwrite", description = "Overwrite existing data files for tables")
    public boolean overwrite = DEFAULT_OVERWRITE;

    public Session toSession()
    {
        validateProperties();
        return new Session(scale,
                directory,
                suffix,
                toTableOptional(table),
                nullString,
                separator,
                doNotTerminate,
                noSexism,
                parallelism,
                overwrite);
    }

    private static Optional<Table> toTableOptional(String table)
    {
        if (table == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Table.valueOf(table.toUpperCase()));
        }
        catch (IllegalArgumentException e) {
            throw new InvalidOptionException("table", table);
        }
    }

    private void validateProperties()
    {
        if (scale < 0 || scale > 100000) {
            throw new InvalidOptionException("scale", Float.toString(scale), "Scale must be greater than 0 and less than 100000");
        }
        if (directory.equals("")) {
            throw new InvalidOptionException("directory", directory, "Directory cannot be an empty string");
        }
        if (suffix.equals("")) {
            throw new InvalidOptionException("suffix", suffix, "Suffix cannot be an empty string");
        }
        if (parallelism < 1) {
            throw new InvalidOptionException("parallelism", Integer.toString(parallelism), "Parallelism must be >= 1");
        }
    }
}

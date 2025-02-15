package pro.civitaspo.embulk.input.http_json.config;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.embulk.base.restclient.RestClientInputTaskBase;
import org.embulk.util.config.Config;
import org.embulk.util.config.ConfigDefault;
import org.embulk.util.config.Task;
import pro.civitaspo.embulk.input.http_json.config.validation.annotations.ValueOfEnum;

public interface PluginTask extends RestClientInputTaskBase {
    public enum URIScheme {
        http,
        https;

        public static URIScheme of(String value) {
            return URIScheme.valueOf(value.toLowerCase(Locale.ENGLISH));
        }
    }

    @Config("scheme")
    @ConfigDefault("\"https\"")
    @ValueOfEnum(enumClass = URIScheme.class)
    public String getScheme();

    @Config("host")
    @NotBlank
    public String getHost();

    @Config("port")
    @ConfigDefault("null")
    public Optional<@Min(0) @Max(65535) Integer> getPort();

    @Config("path")
    @ConfigDefault("null")
    public Optional<@NotBlank String> getPath();

    @Config("headers")
    @ConfigDefault("[]")
    public List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotBlank String>> getHeaders();

    public static enum RequestMethod {
        CONNECT,
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        HEAD,
        OPTIONS,
        TRACE;

        public static RequestMethod of(String value) {
            return RequestMethod.valueOf(value.toUpperCase(Locale.ENGLISH));
        }
    }

    @Config("method")
    @ConfigDefault("\"GET\"")
    @ValueOfEnum(enumClass = RequestMethod.class, caseSensitive = false)
    public String getMethod();

    @Config("params")
    @ConfigDefault("[]")
    public List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotNull Object>> getParams();

    @Config("body")
    @ConfigDefault("null")
    public Optional<JsonNode> getBody();

    @Config("success_condition")
    @ConfigDefault("\".status_code_class == 200\"")
    public @NotBlank String getSuccessCondition();

    @Config("transformer")
    @ConfigDefault("\"[.response_body]\"")
    public @NotBlank String getTransformer();

    @Config("transformed_json_column_name")
    @ConfigDefault("\"payload\"")
    public @NotBlank String getTransformedJsonColumnName();

    @Config("extract_transformed_json_array")
    @ConfigDefault("true")
    public boolean getExtractTransformedJsonArray();

    public interface PagerOption extends Task {
        @Config("initial_params")
        @ConfigDefault("[]")
        public List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotNull Object>>
                getInitialParams();

        @Config("next_params")
        @ConfigDefault("[]")
        public List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotBlank Object>>
                getNextParams();

        @Config("next_body_transformer")
        @ConfigDefault("\".request_body\"")
        public String getNextBodyTransformer();

        @Config("while")
        @ConfigDefault("\"false\"")
        public @NotBlank String getWhile();

        @Config("interval_millis")
        @ConfigDefault("100")
        public @Min(0) long getIntervalMillis();
    }

    @Config("pager")
    @ConfigDefault("{}")
    public PagerOption getPager();

    public interface RetryOption extends Task {
        @Config("condition")
        @ConfigDefault("\"true\"")
        public String getCondition();

        @Config("max_retries")
        @ConfigDefault("7")
        public int getMaxRetries();

        @Config("initial_interval_millis")
        @ConfigDefault("1000")
        public int getInitialIntervalMillis();

        @Config("max_interval_millis")
        @ConfigDefault("60000")
        public int getMaxIntervalMillis();
    }

    @Config("retry")
    @ConfigDefault("{}")
    public RetryOption getRetry();

    @Config("show_request_body_on_error")
    @ConfigDefault("true")
    @NotNull
    public Boolean getShowRequestBodyOnError();

    public interface PrepareOption extends Task {
        public interface RequestOption extends Task {
            @Config("name")
            @NotBlank
            public String getName();

            @Config("scheme")
            @ConfigDefault("null")
            public Optional<@ValueOfEnum(enumClass = URIScheme.class) String> getScheme();

            @Config("host")
            public Optional<@NotBlank String> getHost();

            @Config("port")
            @ConfigDefault("null")
            public Optional<@Min(0) @Max(65535) Integer> getPort();

            @Config("path")
            @ConfigDefault("null")
            public Optional<@NotBlank String> getPath();

            @Config("headers")
            @ConfigDefault("null")
            public Optional<List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotBlank String>>>
                    getHeaders();

            @Config("method")
            @ConfigDefault("null")
            public Optional<
                            @ValueOfEnum(enumClass = RequestMethod.class, caseSensitive = false)
                            String>
                    getMethod();

            @Config("params")
            @ConfigDefault("null")
            public Optional<List<@Size(min = 1, max = 1) Map<@NotBlank String, @NotNull Object>>>
                    getParams();

            @Config("body")
            @ConfigDefault("null")
            public Optional<JsonNode> getBody();

            @Config("success_condition")
            @ConfigDefault("null")
            public Optional<@NotBlank String> getSuccessCondition();

            @Config("retry")
            @ConfigDefault("null")
            public Optional<RetryOption> getRetry();
        }

        @Config("requests")
        @ConfigDefault("[]")
        public List<RequestOption> getRequests();

        public interface AssignToOption extends Task {
            @Config("params")
            @ConfigDefault("[]")
            public List<Map<@NotBlank String, @NotNull String>> getParams();

            @Config("body")
            @ConfigDefault("null")
            public Optional<String> getBody();
        }

        @Config("assign_to")
        public AssignToOption getAssignTo();
    }

    @Config("prepare")
    @ConfigDefault("[]")
    public List<PrepareOption> getPrepare();

    @Config("default_timezone")
    @ConfigDefault("\"UTC\"")
    @NotBlank
    public String getDefaultTimeZoneId();

    @Config("default_timestamp_format")
    @ConfigDefault("\"%Y-%m-%d %H:%M:%S.%N %z\"")
    @NotNull
    public String getDefaultTimestampFormat();

    @Config("default_date")
    @ConfigDefault("\"1970-01-01\"")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    public String getDefaultDate();
}

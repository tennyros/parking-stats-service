package com.gitverse.testcakes.parkings.validation;

import lombok.experimental.UtilityClass;

/**
 * Utility class containing regular expression pattern for validating Russian license plates.
 * This pattern ensures that license plates conform to the official Russian format.
 *
 * @author vadim_23
 */
@UtilityClass
public final class LicensePlatePatterns {

    /**
     * Regular expression pattern for all types of Russian license plates.<p>
     * Supports the following formats:<p>
     * - Standard: A000AA000 (e.g., А123ВС777)<p>
     * - Commercial: AA000A000 (e.g., АА123А777)<p>
     * - Motorcycle: 0000AA000 (e.g., 1234АА777)<p>
     * - Trailer: AA000A000 (e.g., АА123А777)<p>
     *
     * Allowed characters:<p>
     * - Letters: А, В, Е, К, М, Н, О, Р, С, Т, У, Х<p>
     * - Digits: 0-9<p>
     * - Region code: 2 or 3 digits<p>
     */
    public static final String RUSSIAN_LICENSE_PLATE = 
            "^([АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}|[АВЕКМНОРСТУХ]{2}\\d{3}[АВЕКМНОРСТУХ]|\\d{4}[АВЕКМНОРСТУХ]{2})\\d{2,3}$";
} 
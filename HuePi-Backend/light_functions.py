def translate_temperature_to_hsv_color(input_temp, temp_min, temp_max, hsv_color_min, hsv_color_max):
    if input_temp <= temp_min:
        return hsv_color_min
    elif input_temp >= temp_max:
        return hsv_color_max
    else:
        # Figure out how 'wide' each range is
        temp_span = temp_max - temp_min
        hsv_color_span = hsv_color_max - hsv_color_min

        # Convert the temp range into a 0-1 range (float)
        value_scaled = float(input_temp - temp_min) / float(temp_span)

        # Convert the 0-1 range into a value in the hsv range.
        return hsv_color_min + (value_scaled * hsv_color_span)

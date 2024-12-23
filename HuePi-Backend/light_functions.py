def translate_temperature_to_hsv_color(input_temp, temp_min, temp_max, hsv_color_min, hsv_color_max):
    if input_temp <= temp_min:
        return hsv_color_min
    if input_temp >= temp_max:
        return hsv_color_max
    
    # Determine the range of the input temperatures
    temp_span = temp_max - temp_min

    # Scale the temperature into a 0-1 range
    value_scaled = float(input_temp - temp_min) / float(temp_span)

    # Handle the circular nature of the hue
    if hsv_color_min <= hsv_color_max:
        # Normal case, no wrapping needed
        hue_color_span = hsv_color_max - hsv_color_min
        return hsv_color_min + (value_scaled * hue_color_span)
    else:
        # Wrapping case
        hue_color_span = (1 - hsv_color_min) + hsv_color_max
        hue = hsv_color_min + (value_scaled * hue_color_span)
        if hue > 1:  # Wrap around the circle
            hue -= 1
        return hue

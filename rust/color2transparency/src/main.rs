use image::io::Reader as ImageReader;


fn main() {

    let args = std::env::args();
    let file_name_arg = args.skip(1).collect::<Vec<String>>();
    let file_name_option = file_name_arg.get(1);
    let param_r = file_name_arg.get(2);
    let param_g = file_name_arg.get(3);
    let param_b = file_name_arg.get(4);
    let param_threshold = file_name_arg.get(5);

    let r_value = {if param_r.is_some() {param_r.unwrap().parse::<u8>().unwrap()} else {0}};
    let g_value = {if param_g.is_some() {param_g.unwrap().parse::<u8>().unwrap()} else {0}};
    let b_value = {if param_b.is_some() {param_b.unwrap().parse::<u8>().unwrap()} else {0}};
    let threshold_value = {if param_threshold.is_some() {param_threshold.unwrap().parse::<i32>().unwrap()} else {0}};

    if !file_name_option.is_none() {
        let file_name = file_name_option.unwrap();
        println!("Argument is: {}", file_name);
        
        let img_open = ImageReader::open(file_name);
        
        
        if img_open.is_ok(){
            println!("OK OPEN!");
            let mut img_reader = img_open.unwrap();
            img_reader.no_limits();
            let img = img_reader.decode().unwrap();
            let mut modified_img = img.to_rgba8();

            for pixel in modified_img.pixels_mut() {
                //println!("Pixel: {} {} {} {}", pixel[0],pixel[1],pixel[2], pixel[3]);
                if i32::from(pixel[0]) >= i32::from(r_value) - threshold_value
                && i32::from(pixel[0]) <= i32::from(r_value) + threshold_value
                && i32::from(pixel[1]) >= i32::from(g_value) - threshold_value
                && i32::from(pixel[1]) <= i32::from(g_value) + threshold_value
                && i32::from(pixel[2]) >= i32::from(b_value) - threshold_value
                && i32::from(pixel[2]) <= i32::from(b_value) + threshold_value {
                   pixel[3] = 0;
                }
            }

            let _ = modified_img.save("out.png");
            
        } else {
            println!("Dir: {:?}", std::env::current_dir().unwrap());
            println!("Open NOT ok: {:?}", img_open.err());
        }

        //let img = ImageReader::open(file_name)?.decode()?;
        //println!("{:?}", img.unwrap())

    } else {
        println!("NO ARGUMENT!");
        println!("Usage: <file> <r> <g> <b> <range>")
    }
}
usage() {
    cat << EOM
Usage: install.sh [OPTIONS]...

Available options:
    --chaos     Compile with the chaos monkey
    -h, --help  Print this help
EOM
}

parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
        --chaos)
            CHAOS=1
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo ""
            usage
            exit 1
            ;;
        esac
    done
}
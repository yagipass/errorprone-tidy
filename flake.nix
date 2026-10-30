{
  description = "Additional Error Prone checks for tidier Java code";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts = {
      url = "github:hercules-ci/flake-parts";
      inputs.nixpkgs-lib.follows = "nixpkgs";
    };
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    git-hooks = {
      url = "github:cachix/git-hooks.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      imports = [
        inputs.treefmt-nix.flakeModule
        inputs.git-hooks.flakeModule
      ];

      systems = [
        "aarch64-darwin"
        "aarch64-linux"
        "x86_64-linux"
      ];

      perSystem =
        {
          config,
          pkgs,
          lib,
          ...
        }:
        let
          jdk = pkgs.jdk25;
        in
        {
          treefmt = {
            projectRootFile = "flake.nix";
            flakeCheck = false;
            programs.nixfmt.enable = true;
          };

          pre-commit = {
            settings.excludes = [ "^CHANGELOG\\.md$" ];

            settings.hooks = {
              treefmt.enable = true;

              gitleaks.enable = true;

              convco.enable = true;

              end-of-file-fixer.enable = true;
              trim-trailing-whitespace.enable = true;
              check-merge-conflicts = {
                enable = true;
                args = [ "--assume-in-merge" ];
              };
              check-added-large-files = {
                enable = true;
                args = [ "--enforce-all" ];
              };
              detect-private-keys.enable = true;

              typos = {
                enable = true;
                files = "\\.md$";
              };
              markdownlint = {
                enable = true;
                settings.configuration = {
                  default = true;
                  MD013 = false;
                  MD031.list_items = false;
                  MD033 = false;
                  MD060 = false;
                };
              };

              jactionlint = {
                enable = true;
                package = pkgs.jactionlint;
                entry = "${lib.getExe pkgs.jactionlint} --shellcheck=${lib.getExe pkgs.shellcheck} --pyflakes=${lib.getExe pkgs.python3Packages.pyflakes}";
                files = "^\\.github/|(^|/)action\\.ya?ml$";
                pass_filenames = false;
              };
            };
          };

          devShells.default = pkgs.mkShell {
            packages = [ jdk ] ++ config.pre-commit.settings.enabledPackages;

            JAVA_HOME = jdk.home;

            shellHook = config.pre-commit.shellHook + ''
              export SOURCE_DATE_EPOCH="$(git log -1 --format=%ct)"
            '';
          };
        };
    };
}
